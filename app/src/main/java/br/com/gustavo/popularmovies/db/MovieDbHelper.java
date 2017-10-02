package br.com.gustavo.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gustavomagalhaes on 9/10/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";

    private static final String SQL_CREATE_FAVORITE_ENTRY =
            "CREATE TABLE " +
                    FavoriteContract.FavoriteEntry.TABLE_NAME + " (" +
                    FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoriteContract.FavoriteEntry.COLUMN_NAME_ID_MOVIE + " INTEGER, " +
                    FavoriteContract.FavoriteEntry.COLUMN_NAME_TITLE + " TEXT, " +
                    FavoriteContract.FavoriteEntry.COLUMN_NAME_OVERVIEW + " TEXT, " +
                    FavoriteContract.FavoriteEntry.COLUMN_NAME_RATED + " REAL, " +
                    FavoriteContract.FavoriteEntry.COLUMN_NAME_RELEASEDATE + " NUMERIC, " +
                    FavoriteContract.FavoriteEntry.COLUMN_NAME_IMAGE + " BLOB " +
                    ")";

    private static final String SQL_DROP_FAVORITE_ENTRY = "DROP TABLE IF EXITS " + FavoriteContract.FavoriteEntry.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DROP_FAVORITE_ENTRY);

        onCreate(sqLiteDatabase);
    }
}
