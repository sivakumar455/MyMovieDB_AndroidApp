package com.example.android.mymovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieIntentActivity extends AppCompatActivity{

    private SQLiteDatabase mDb;
    ImageButton imgbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_intent);
        String trStr = null;

        TextView title = findViewById(R.id.title);
        TextView overview = findViewById(R.id.overview);
        TextView release_date = findViewById(R.id.release_date);
        TextView user_rate = findViewById(R.id.user_rate);
        ImageView poster = findViewById(R.id.poster);
        final Intent intentStarted = getIntent();
        Button fav = findViewById(R.id.fav_button);
        imgbutton = findViewById(R.id.tr_button);
        final MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getApplicationContext());

        if (intentStarted.hasExtra(Intent.EXTRA_TEXT)){
            HashMap<String,String> movieDet = (HashMap<String, String>) intentStarted.getSerializableExtra(Intent.EXTRA_TEXT);

            title.setText(movieDet.get("original_title"));
            movieDet.put("poster","https://image.tmdb.org/t/p/w500/"+movieDet.get("poster_path"));
            Picasso.with(getApplicationContext()).load(movieDet.get("poster")).placeholder(R.drawable.ic_launcher_background).into(poster);
            overview.setText(movieDet.get("overview"));
            release_date.setText(movieDet.get("release_date"));
            user_rate.setText(movieDet.get("vote_average"));

            String id = movieDet.get("movie_id");

            MyMovieAsyncTask mytask = new MyMovieAsyncTask();
            mytask.execute(id);
            //Log.v("TR1",trStr);
            /*if(trStr != null){
                MovieDetails myTrList = new MovieDetails(trStr);
                ArrayList<String> myVid = myTrList.getTrailerList();
                setOnClick(imgbutton, myVid);
            }*/
        }
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("TAG","INserting ");
                mDb = moviesDbHelper.getWritableDatabase();
                HashMap<String, String> movieDet = (HashMap<String, String>) intentStarted.getSerializableExtra(Intent.EXTRA_TEXT);
                addFavMovies(movieDet);
                mDb.close();
                Log.v("TAG","After INserting ");
            }
        });

    }

    private void setOnClick(final ImageButton imgbutton, final ArrayList<String> myVid){
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(myVid.get(0))));
                Log.i("Video", "Video Playing....");
            }
        });
    }


    private class MyMovieAsyncTask extends AsyncTask<String,Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpRequest trReq = new HttpRequest("videos",strings[0]);
            return trReq.getJsonString();
        }

        @Override
        protected void onPostExecute(final String trStr) {
            if(trStr != null){
                MovieDetails myTrList = new MovieDetails(trStr);
                ArrayList<String> myVid = myTrList.getTrailerList();
                setOnClick(imgbutton, myVid);
            }
            super.onPostExecute(trStr);
        }
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
