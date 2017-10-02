package br.com.gustavo.popularmovies.db;

import android.net.Uri;
import android.provider.BaseColumns;

import br.com.gustavo.popularmovies.R;

/**
 * Created by gustavomagalhaes on 9/10/17.
 */

public final class FavoriteContract {

    public static final String PATH_AUTHORITY_URI = "br.com.gustavo.popularmovies";

    private static final Uri PATH_URI = Uri.parse("content://" + PATH_AUTHORITY_URI);

    public static class FavoriteEntry implements BaseColumns {

        public static final Uri PATH_FAVORITE_URI = PATH_URI.buildUpon().appendPath(FavoriteEntry.TABLE_NAME).build();

        public static final String TABLE_NAME = "favorite";
        public static final String COLUMN_NAME_ID_MOVIE = "id_movie";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RATED = "rated";
        public static final String COLUMN_NAME_RELEASEDATE = "release_date";
        public static final String COLUMN_NAME_IMAGE = "image";
    }
}
