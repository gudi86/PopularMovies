package br.com.gustavo.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.gustavo.popularmovies.db.FavoriteContract;
import br.com.gustavo.popularmovies.model.Movie;
import br.com.gustavo.popularmovies.model.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterGridMovies.MovieOnClickAdapter, Callback<Result<Movie>> {

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static String FLD_MOVIES_STATE = "movies";
    private final static String FLD_FILTER_STATE = "filter";

    public final static String SAVE_MOVIE = "SAVE_MOVIE";
    private static final int LOADER_FETCH_FAVORITE = 1003;

    private int currentFilter;

    private RecyclerView recycler;

    private ProgressBar pbLoadWait;

    private LinearLayout llErrorLoad;
    private Movie[] movies = null;
    private int filterSelected = NetworkUtils.URL_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = (RecyclerView) findViewById(R.id.rv_grid_movies);
        pbLoadWait = (ProgressBar) findViewById(R.id.pb_loading_movies);
        llErrorLoad = (LinearLayout) findViewById(R.id.ll_error_load_movie);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT?2:3);

        recycler.setLayoutManager(gridLayoutManager);

        recycler.setHasFixedSize(true);

        AdapterGridMovies adapterGridMovies = new AdapterGridMovies(this);

        recycler.setAdapter(adapterGridMovies);

        if (savedInstanceState != null && savedInstanceState.containsKey(FLD_MOVIES_STATE)) {
            startLoadMovies();
            currentFilter = savedInstanceState.getInt(FLD_FILTER_STATE);
            movies = (Movie[]) savedInstanceState.getParcelableArray(FLD_MOVIES_STATE);
            finishLoadMovies(false);
            adapterGridMovies.setNewMovies(movies);
        } else {
            currentFilter = NetworkUtils.URL_POPULAR;
            startLoadMovies();
            NetworkUtils.createMovieServiceBySort(currentFilter).enqueue(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(LOADER_FETCH_FAVORITE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArray(FLD_MOVIES_STATE, movies);
        outState.putInt(FLD_FILTER_STATE, currentFilter);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClickAdapter(Movie movie) {
        Log.d(TAG, "User clicked on view movie and data is " + movie.toString());
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(SAVE_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        int filterClicked = -1;
        if (R.id.action_order_most_popular == id && filterSelected != NetworkUtils.URL_POPULAR) {
            Log.d(TAG, "User clicked on order by most popular.");
            filterClicked = filterSelected = NetworkUtils.URL_POPULAR;
        } else if (R.id.action_order_top_rated == id && filterSelected != NetworkUtils.URL_RATED) {
            Log.d(TAG, "User clicked on order by top rated.");
            filterClicked = filterSelected = NetworkUtils.URL_RATED;
        } else if (R.id.action_order_favorite == id && filterSelected != NetworkUtils.FAVORITE) {
            Log.d(TAG, "User clicked on order by top rated.");
            filterClicked = filterSelected = NetworkUtils.FAVORITE;
        }

        if (filterClicked < 0) {
            return super.onOptionsItemSelected(item);
        }

        if (filterSelected == NetworkUtils.FAVORITE) {
            startLoadMovies();
            if (getSupportLoaderManager().getLoader(LOADER_FETCH_FAVORITE) == null) {
                getSupportLoaderManager().initLoader(LOADER_FETCH_FAVORITE, null, new LoaderFetchFavotire()).forceLoad();
            } else {
                getSupportLoaderManager().restartLoader(LOADER_FETCH_FAVORITE, null, new LoaderFetchFavotire()).forceLoad();
            }
            return true;
        }

        currentFilter = filterSelected;
        startLoadMovies();
        NetworkUtils.createMovieServiceBySort(currentFilter).enqueue(this);
        return true;

    }

    private void startLoadMovies() {
        recycler.setVisibility(View.INVISIBLE);
        llErrorLoad.setVisibility(View.INVISIBLE);
        pbLoadWait.setVisibility(View.VISIBLE);
        // It sends list to top when list on bottom
        recycler.getLayoutManager().scrollToPosition(0);
    }

    private void finishLoadMovies(Boolean isError) {
        if (isError) {
            ImageView imgError = (ImageView) findViewById(R.id.iv_error);
            TextView msgError = (TextView) findViewById(R.id.tv_msg_error);
            if (filterSelected == NetworkUtils.FAVORITE) {
                imgError.setImageResource(R.drawable.ic_video_off);
                msgError.setText(R.string.txt_msg_error_load_favorite);
            } else {
                imgError.setImageResource(R.drawable.ic_signal_wifi_off);
                msgError.setText(R.string.txt_msg_error_load_movies);
            }
            llErrorLoad.setVisibility(View.VISIBLE);
            Button button = (Button) llErrorLoad.findViewById(R.id.bt_try);
            button.setVisibility(View.VISIBLE);
            if (filterSelected == NetworkUtils.FAVORITE) {
                button.setVisibility(View.GONE);
            } else {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startLoadMovies();
                        NetworkUtils.createMovieServiceBySort(currentFilter).enqueue(MainActivity.this);
                    }
                });
            }

        } else {
            recycler.setVisibility(View.VISIBLE);
        }
        pbLoadWait.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResponse(@NonNull Call<Result<Movie>> call, @NonNull Response<Result<Movie>> response) {
        if (response.body() == null || response.body().getResults() == null) {
            finishLoadMovies(true);
            return ;
        }

        finishLoadMovies(false);
        movies = response.body().getResults();
        ((AdapterGridMovies) recycler.getAdapter()).setNewMovies(movies);
    }

    @Override
    public void onFailure(@NonNull Call<Result<Movie>> call, @NonNull Throwable t) {
        finishLoadMovies(true);
    }

    private class LoaderFetchFavotire implements LoaderManager.LoaderCallbacks<List<Movie>> {

        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<Movie>>(getBaseContext()) {
                @Override
                public List<Movie> loadInBackground() {
                    Cursor cursor = getContentResolver().query(FavoriteContract.FavoriteEntry.PATH_FAVORITE_URI,
                            null,
                            null,
                            null,
                            null);

                    List<Movie> favotites = new ArrayList<>();

                    while(cursor.moveToNext()) {
                        Integer idMovie = cursor.getInt(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_NAME_ID_MOVIE));
                        String title = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_NAME_TITLE));
                        String overview = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_NAME_OVERVIEW));
                        Double rated = cursor.getDouble(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_NAME_RATED));
                        Long timestamp = cursor.getLong(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_NAME_RELEASEDATE));
                        byte[] image = cursor.getBlob(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_NAME_IMAGE));

                        favotites.add(new Movie(idMovie, title, overview, rated, new Date(timestamp), "", image));
                    }
                    return favotites;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
            if (data.size() > 0) {
                finishLoadMovies(false);
                movies = new Movie[data.size()];
                movies = data.toArray(movies);
                ((AdapterGridMovies) recycler.getAdapter()).setNewMovies(movies);
            } else {
                finishLoadMovies(true);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {

        }
    }
}
