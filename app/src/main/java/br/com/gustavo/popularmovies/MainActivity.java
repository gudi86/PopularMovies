package br.com.gustavo.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AdapterGridMovies.MovieOnClickAdapter{

    private final static String TAG = MainActivity.class.getSimpleName();

    private final static String FLD_MOVIES_STATE = "movies";
    private final static String FLD_FILTER_STATE = "filter";

    public final static String SAVE_MOVIE = "SAVE_MOVIE";

    private int currentFilter;

    private RecyclerView recycler;

    private ProgressBar pbLoadWait;

    private LinearLayout llErrorLoad;
    private Movie[] movies = null;

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
            new RequestMovie().execute(NetworkUtils.buildUrlMovieBySort(currentFilter));
        }
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

        int filterSelected = -1;
        if (R.id.action_order_most_popular == id) {
            Log.d(TAG, "User clicked on order by most popular.");
            filterSelected = NetworkUtils.URL_POPULAR;
        } else if (R.id.action_order_top_rated == id) {
            Log.d(TAG, "User clicked on order by top rated.");
            filterSelected = NetworkUtils.URL_RATED;
        }

        if (filterSelected > -1) {
            currentFilter = filterSelected;
            new RequestMovie().execute(NetworkUtils.buildUrlMovieBySort(currentFilter));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startLoadMovies() {
        recycler.setVisibility(View.INVISIBLE);
        llErrorLoad.setVisibility(View.INVISIBLE);
        pbLoadWait.setVisibility(View.VISIBLE);
    }

    private void finishLoadMovies(Boolean isError) {
        if (isError) {
            llErrorLoad.setVisibility(View.VISIBLE);
            Button button = (Button) llErrorLoad.findViewById(R.id.bt_try);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new RequestMovie().execute(NetworkUtils.buildUrlMovieBySort(currentFilter));
                }
            });

        } else {
            recycler.setVisibility(View.VISIBLE);
        }
        pbLoadWait.setVisibility(View.INVISIBLE);
    }

    private class RequestMovie extends AsyncTask<URL, Void, Movie[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startLoadMovies();
        }

        @Override
        protected Movie[] doInBackground(URL... urls) {
            if (urls == null) {
                return null;
            }

            Movie[] movies = null;
            try {

                String response = NetworkUtils.getResponseFromHttpUrl(urls[0]);

                if (response != null) {
                    movies = UtilsJsonMovie.convertJsonObjectToMovies(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies == null) {
                finishLoadMovies(true);
                return ;
            }

            finishLoadMovies(false);
            MainActivity.this.movies = movies;
            ((AdapterGridMovies) recycler.getAdapter()).setNewMovies(MainActivity.this.movies);
        }
    }
}
