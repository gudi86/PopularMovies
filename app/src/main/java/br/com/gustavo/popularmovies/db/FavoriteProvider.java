package br.com.gustavo.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.gustavo.popularmovies.R;

/**
 * Created by gustavomagalhaes on 9/17/17.
 */

public class FavoriteProvider extends ContentProvider {

    private static final int FAVORITE = 50;
    private static final int FAVORITE_WITH_ID = 51;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(FavoriteContract.PATH_AUTHORITY_URI, FavoriteContract.FavoriteEntry.TABLE_NAME, FAVORITE);
        matcher.addURI(FavoriteContract.PATH_AUTHORITY_URI, FavoriteContract.FavoriteEntry.TABLE_NAME + "/#", FAVORITE_WITH_ID);
    }

    private MovieDbHelper dbHelper;

    @Override
    public boolean onCreate() {

        dbHelper = new MovieDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int match = matcher.match(uri);

        Cursor cursor = null;
        switch (match) {
            case FAVORITE:
                cursor = dbHelper.getReadableDatabase().query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVORITE_WITH_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        FavoriteContract.FavoriteEntry.TABLE_NAME,
                        null,
                        FavoriteContract.FavoriteEntry.COLUMN_NAME_ID_MOVIE + " = ?",
                        new String[]{uri.getPathSegments().get(1)},
                        null,
                        null,
                        null);
                break;
            default:
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int match = matcher.match(uri);
        Uri urlResult = null;
        switch (match) {
            case FAVORITE:
                long id = dbHelper.getWritableDatabase().insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    urlResult = ContentUris.withAppendedId(uri, id);
                }
                break;
            default:
                break;
        }
        return urlResult;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        int match = matcher.match(uri);

        int numberOfRows = 0;
        switch (match) {
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                numberOfRows = dbHelper.getWritableDatabase().delete(FavoriteContract.FavoriteEntry.TABLE_NAME, FavoriteContract.FavoriteEntry.COLUMN_NAME_ID_MOVIE + "=?", new String[]{id});
                break;
            default:
                break;
        }

        return numberOfRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
