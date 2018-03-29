package com.example.android.mymovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author  Siva Kumar Padala
 * @version 1.0
 * @since   29/03/18
 */

class MovieDbContract {

    public static final String AUTHORITY = "com.example.android.mymovies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

    public static final String PATH_TASKS = "MovieDb";

    public static final class MovieDb implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "MovieDb";
        public static final String COLUMN_MOVIE_ID = "MovieId";
        public static final String COLUMN_MOVIE_NAME = "MovieName";
        public static final String COLUMN_POSTER_ID = "PosterId";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releasedate";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_VOTE_AVG = "VoteAvg";
    }
}
