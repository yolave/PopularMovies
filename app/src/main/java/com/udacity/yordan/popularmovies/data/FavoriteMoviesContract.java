package com.udacity.yordan.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Yordan on 3/18/2017.
 */

public class FavoriteMoviesContract {

    public static final String AUTHORITY = "com.udacity.yordan.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorite_movies";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
    }
}
