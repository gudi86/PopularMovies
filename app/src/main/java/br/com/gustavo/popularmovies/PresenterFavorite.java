package br.com.gustavo.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.nio.ByteBuffer;

import br.com.gustavo.popularmovies.db.FavoriteContract;
import br.com.gustavo.popularmovies.model.Movie;

import static br.com.gustavo.popularmovies.DetailsActivity.LOADER_ADD_FAVORITE;
import static br.com.gustavo.popularmovies.DetailsActivity.LOADER_REMOVE_FAVORITE;

/**
 * Created by gustavomagalhaes on 9/22/17.
 */

public class PresenterFavorite {

    private final static String TAG = PresenterFavorite.class.getName();
    private static final String FAVORITE_MOVIE = "FAVORITE_MOVIE";

    private final Context context;

    private View buttonFav;

    public PresenterFavorite(Context context) {
        this.context = context;
    }


    public void onClickAddFavorite(View view, final Movie movie) {

        buttonFav = view;

        if (view.getTag() == null) {
            Glide.with(context)
                    .asBitmap()
                    .load(NetworkUtils.buildUriImageBy(movie, context.getResources().getDisplayMetrics().densityDpi))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            Log.d(TAG, "Geting bitmap image");
                            ByteBuffer byteBuffer = ByteBuffer.allocate(resource.getByteCount());
                            resource.copyPixelsToBuffer(byteBuffer);

                            Movie saveMovie = new Movie(movie.getId(),
                                    movie.getTitle(),
                                    movie.getOverview(),
                                    movie.getRated(),
                                    movie.getReleaseDate(),
                                    movie.getPosterPath(),
                                    byteBuffer.array());

                            Bundle bundle = new Bundle();
                            bundle.putParcelable(FAVORITE_MOVIE, saveMovie);

                            if(((DetailsActivity)context).getSupportLoaderManager().getLoader(LOADER_ADD_FAVORITE) == null) {
                                ((DetailsActivity) context).getSupportLoaderManager().initLoader(LOADER_ADD_FAVORITE, bundle, new LoaderAddFavorite()).forceLoad();
                            } else {
                                ((DetailsActivity) context).getSupportLoaderManager().restartLoader(LOADER_ADD_FAVORITE, bundle, new LoaderAddFavorite()).forceLoad();
                            }
                        }
                    });
        } else if (((Integer)view.getTag()).equals(movie.getId())) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(FAVORITE_MOVIE, movie);
            if(((DetailsActivity)context).getSupportLoaderManager().getLoader(LOADER_REMOVE_FAVORITE) == null) {
                ((DetailsActivity) context).getSupportLoaderManager().initLoader(LOADER_REMOVE_FAVORITE, bundle, new LoaderRemoveFavorite()).forceLoad();
            } else {
                ((DetailsActivity) context).getSupportLoaderManager().restartLoader(LOADER_REMOVE_FAVORITE, bundle, new LoaderRemoveFavorite()).forceLoad();
            }
        }
    }

    public class LoaderAddFavorite implements LoaderManager.LoaderCallbacks<Uri> {

        private Movie movie;

        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<Uri> onCreateLoader(int id, Bundle args) {

            movie = args.getParcelable(FAVORITE_MOVIE);

            return new AsyncTaskLoader<Uri>((DetailsActivity) context) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                }

                @Override
                public Uri loadInBackground() {

                    ContentValues values = new ContentValues();
                    values.put(FavoriteContract.FavoriteEntry.COLUMN_NAME_ID_MOVIE, movie.getId());
                    values.put(FavoriteContract.FavoriteEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());
                    values.put(FavoriteContract.FavoriteEntry.COLUMN_NAME_IMAGE, movie.getPosterPath());
                    values.put(FavoriteContract.FavoriteEntry.COLUMN_NAME_RATED, movie.getRated());
                    values.put(FavoriteContract.FavoriteEntry.COLUMN_NAME_RELEASEDATE, movie.getReleaseDate().getTime());
                    values.put(FavoriteContract.FavoriteEntry.COLUMN_NAME_TITLE, movie.getTitle());
                    values.put(FavoriteContract.FavoriteEntry.COLUMN_NAME_IMAGE, movie.getImage());

                    return context.getContentResolver().insert(FavoriteContract.FavoriteEntry.PATH_FAVORITE_URI, values);
                }

            };
        }

        @Override
        public void onLoadFinished(Loader<Uri> loader, Uri data) {
            if (data != null) {
                Toast.makeText(context, R.string.msg_save_favority, Toast.LENGTH_LONG).show();
                ((Button)buttonFav).setText(R.string.lbl_remove_mark_favorite);
                ((Button)buttonFav).setTag(movie.getId());
            } else {
                Toast.makeText(context, R.string.msg_not_save_favority, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<Uri> loader) {
            Log.d(TAG, "onLoaderReset");
        }
    }

    public class LoaderRemoveFavorite implements LoaderManager.LoaderCallbacks<Integer> {

        private Movie movie;

        @SuppressLint("StaticFieldLeak")
        @Override
        public Loader<Integer> onCreateLoader(int id, Bundle args) {

            movie = args.getParcelable(FAVORITE_MOVIE);

            return new AsyncTaskLoader<Integer>((DetailsActivity) context) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                }

                @Override
                public Integer loadInBackground() {
                    return context.getContentResolver().delete(
                            ContentUris.withAppendedId(FavoriteContract.FavoriteEntry.PATH_FAVORITE_URI, movie.getId()),
                            null,
                            null);
                }

            };
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader, Integer data) {
            if (data != null) {
                Toast.makeText(context, R.string.msg_remove_favority, Toast.LENGTH_LONG).show();
                ((Button)buttonFav).setText(R.string.lbl_add_mark_favorite);
                ((Button)buttonFav).setTag(null);
            } else {
                Toast.makeText(context, R.string.msg_not_remove_favority, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<Integer> loader) {
            Log.d(TAG, "onLoaderReset");
        }
    }
}
