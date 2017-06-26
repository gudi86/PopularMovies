package br.com.gustavo.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterGridMovies.MovieOnClickAdapter, Callback<Result>{

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
            startLoadMovies();
            NetworkUtils.createMovieServiceBySort(currentFilter).enqueue(this);
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
            startLoadMovies();
            NetworkUtils.createMovieServiceBySort(currentFilter).enqueue(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            llErrorLoad.setVisibility(View.VISIBLE);
            Button button = (Button) llErrorLoad.findViewById(R.id.bt_try);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startLoadMovies();
                    NetworkUtils.createMovieServiceBySort(currentFilter).enqueue(MainActivity.this);
                }
            });

        } else {
            recycler.setVisibility(View.VISIBLE);
        }
        pbLoadWait.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
        if (response.body() == null || response.body().getResults() == null) {
            finishLoadMovies(true);
            return ;
        }

        finishLoadMovies(false);
        movies = response.body().getResults();
        ((AdapterGridMovies) recycler.getAdapter()).setNewMovies(movies);
    }

    @Override
    public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
        finishLoadMovies(true);
    }
}
