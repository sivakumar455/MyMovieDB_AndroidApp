package com.example.android.mymovies;

import android.provider.BaseColumns;

/**
 * Created by SIPADALA on 2/25/2018.
 */

public class MovieDbContract {
    public static final class MovieDb implements BaseColumns{

        public static final String TABLE_NAME = "MovieDb";
        public static final String COLUMN_MOVIE_ID = "MovieId";
        public static final String COLUMN_MOVIE_NAME = "MovieName";
        public static final String COLUMN_POSTER_ID = "PosterId";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "releasedate";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
