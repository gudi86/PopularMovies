package br.com.gustavo.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import br.com.gustavo.popularmovies.databinding.ActivityDetailsBinding;
import br.com.gustavo.popularmovies.db.FavoriteContract;
import br.com.gustavo.popularmovies.model.Movie;
import br.com.gustavo.popularmovies.model.Result;
import br.com.gustavo.popularmovies.model.Review;
import br.com.gustavo.popularmovies.model.Trailer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterTrailerMovie.OnClickTrailerMovie{

    private static final String ID_MOVIE = "ID_MOVIE";
    private static final int LOADER_QUERY_MOVIE = 1001;
    public static final int LOADER_ADD_FAVORITE = 1000;
    public static final int LOADER_REMOVE_FAVORITE = 1002;
    private Movie movie;
    private Integer itemSeleted = R.id.action_view_trailers;
    private String TAG = DetailsActivity.class.getName();
    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        binding.setPresenter(new PresenterFavorite(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        binding.rvMoreInfo.setLayoutManager(linearLayoutManager);

        binding.rvMoreInfo.setHasFixedSize(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(MainActivity.SAVE_MOVIE)) {
            movie = savedInstanceState.getParcelable(MainActivity.SAVE_MOVIE);
            binding.setMovie(movie);
        } else {
            if (getIntent() != null && getIntent().hasExtra(MainActivity.SAVE_MOVIE)) {
                movie = getIntent().getParcelableExtra(MainActivity.SAVE_MOVIE);
                binding.setMovie(movie);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.txt_msg_error_load_movie);
                builder.setPositiveButton(R.string.lbl_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
        }

        Bundle bundle = new Bundle();
        bundle.putInt(ID_MOVIE, movie.getId());

        if (getSupportLoaderManager().getLoader(LOADER_QUERY_MOVIE) == null) {
            getSupportLoaderManager().initLoader(LOADER_QUERY_MOVIE, bundle, this).forceLoad();
        } else {
            getSupportLoaderManager().restartLoader(LOADER_QUERY_MOVIE, bundle, this).forceLoad();
        }

        loadTrailers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_view_trailers) {
            Log.d(TAG, "Trailers");
            if (itemSeleted != R.id.action_view_trailers) {
                itemSeleted = R.id.action_view_trailers;
                loadTrailers();
            }
        } else if (item.getItemId() == R.id.action_view_reviews) {
            if (itemSeleted != R.id.action_view_reviews) {
                itemSeleted = R.id.action_view_reviews;
                loadReview();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportLoaderManager().destroyLoader(LOADER_QUERY_MOVIE);
        getSupportLoaderManager().destroyLoader(LOADER_ADD_FAVORITE);
        getSupportLoaderManager().destroyLoader(LOADER_REMOVE_FAVORITE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MainActivity.SAVE_MOVIE, movie);
        super.onSaveInstanceState(outState);
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, Movie movie) {
        Glide.with(view.getContext())
                .load(NetworkUtils.buildUrlImageBy(movie, view.getContext().getResources().getDisplayMetrics().densityDpi))
                .into(view);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            @Override
            public Cursor loadInBackground() {
                int idMovie = args.getInt(ID_MOVIE);
                return getContentResolver().query(ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.PATH_FAVORITE_URI,idMovie),
                        null,
                        null,
                        null,
                        null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0) {
            binding.btnFavorite.setText(R.string.lbl_remove_mark_favorite);
            data.moveToFirst();
            int idMovie = data.getInt(data.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_NAME_ID_MOVIE));
            binding.btnFavorite.setTag(idMovie);
        } else {
            binding.btnFavorite.setText(R.string.lbl_add_mark_favorite);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClickTrailer(Trailer trailer) {
        NetworkUtils.openVideoYouTube(this, trailer.getKey());
    }

    private void loadReview() {
        NetworkUtils.fetchReviewBy(movie.getId()).enqueue(new Callback<Result<Review>>() {
            @Override
            public void onResponse(Call<Result<Review>> call, Response<Result<Review>> response) {
                if (response.body() == null || response.body().getResults() == null) {
                    Log.d(TAG, "Response from Review API is null or empyt");
                    return ;
                }
                Review[] reviews = response.body().getResults();
                Log.d(TAG, "List of reviews from movie.");

                binding.tvTitleInfo.setText(R.string.lbl_reviews);

                AdapterReviewMovie adapterMoreInfoMovie = new AdapterReviewMovie(reviews);
                binding.rvMoreInfo.setAdapter(adapterMoreInfoMovie);

            }
            @Override
            public void onFailure(Call<Result<Review>> call, Throwable t) {
                Log.d(TAG, "It has a failure to call Review.", t);
            }
        });
    }

    private void loadTrailers() {
        NetworkUtils.fetchTrailerBy(movie.getId()).enqueue(new Callback<Result<Trailer>>() {
            @Override
            public void onResponse(Call<Result<Trailer>> call, Response<Result<Trailer>> response) {
                if (response.body() == null || response.body().getResults() == null) {
                    Log.d(TAG, "Response from trailer API is null or empyt");
                    return ;
                }
                Trailer[] trailers = response.body().getResults();
                Log.d(TAG, "List of trailer from movie.");

                binding.tvTitleInfo.setText(R.string.lbl_trailes);

                AdapterTrailerMovie adapterMoreInfoMovie = new AdapterTrailerMovie(trailers, DetailsActivity.this);
                binding.rvMoreInfo.setAdapter(adapterMoreInfoMovie);

            }
            @Override
            public void onFailure(Call<Result<Trailer>> call, Throwable t) {
                Log.d(TAG, "It has a failure to call Trailerr.");
            }
        });
    }
}
