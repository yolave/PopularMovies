package com.udacity.yordan.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yordan on 3/18/2017.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoriteMovies.db";

    private static final int VERSION = 1;


    FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE "  + FavoriteMoviesContract.FavoriteEntry.TABLE_NAME + " (" +
                FavoriteMoviesContract.FavoriteEntry._ID                + " INTEGER PRIMARY KEY, " +
                FavoriteMoviesContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteMoviesContract.FavoriteEntry.COLUMN_TITLE    + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
         * Commented because there are not reason (at this moment) to upgrade the database. However, if at some point
         * of the development this action is required, let's follow this steps:
         * 1.- Check the old db version against the new one
         * 2.- If the old version is lower than the new one, so apply the changes (DROP, UPDATE, ALTER, etc)
         */
//        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesContract.FavoriteEntry.TABLE_NAME);
//        onCreate(db);
    }
}
