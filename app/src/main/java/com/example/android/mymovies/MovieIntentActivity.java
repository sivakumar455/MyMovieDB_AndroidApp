package com.example.android.mymovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MovieIntentActivity extends AppCompatActivity {

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_intent);

        TextView title = findViewById(R.id.title);
        TextView overview = findViewById(R.id.overview);
        TextView release_date = findViewById(R.id.release_date);
        TextView user_rate = findViewById(R.id.user_rate);
        ImageView poster = findViewById(R.id.poster);
        final Intent intentStarted = getIntent();
        Button fav = findViewById(R.id.fav_button);
        final MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getApplicationContext());

        if (intentStarted.hasExtra(Intent.EXTRA_TEXT)){
            HashMap<String,String> movieDet = (HashMap<String, String>) intentStarted.getSerializableExtra(Intent.EXTRA_TEXT);

            title.setText(movieDet.get("original_title"));
            movieDet.put("poster","https://image.tmdb.org/t/p/w500/"+movieDet.get("poster_path"));
            Picasso.with(getApplicationContext()).load(movieDet.get("poster")).into(poster);
            overview.setText(movieDet.get("overview"));
            release_date.setText(movieDet.get("release_date"));
            user_rate.setText(movieDet.get("vote_average"));
        }
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("TAG","INserting ");
                mDb = moviesDbHelper.getWritableDatabase();
                HashMap<String, String> movieDet = (HashMap<String, String>) intentStarted.getSerializableExtra(Intent.EXTRA_TEXT);
                addFavMovies(movieDet);
                Log.v("TAG","After INserting ");
            }
        });
    }

    private long addFavMovies(HashMap<String,String> movieDet){
        ContentValues cv = new ContentValues();
        cv.put(MovieDbContract.MovieDb.COLUMN_MOVIE_ID,movieDet.get("movie_id"));
        cv.put(MovieDbContract.MovieDb.COLUMN_MOVIE_NAME,movieDet.get("original_title"));
        cv.put(MovieDbContract.MovieDb.COLUMN_POSTER_ID,movieDet.get("poster_path"));
        cv.put(MovieDbContract.MovieDb.COLUMN_OVERVIEW,movieDet.get("overview"));
        cv.put(MovieDbContract.MovieDb.COLUMN_RELEASE_DATE,movieDet.get("release_date"));
        return mDb.insert(MovieDbContract.MovieDb.TABLE_NAME, null, cv);
    }

}
