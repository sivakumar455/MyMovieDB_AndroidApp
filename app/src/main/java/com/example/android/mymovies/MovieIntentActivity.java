package com.example.android.mymovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author  Siva Kumar Padala
 * @version 1.0
 * @since   29/03/18
 */

public class MovieIntentActivity extends AppCompatActivity{

    private SQLiteDatabase mDb;
    private TextView txtView;
    private LinearLayout rootView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_intent);
        String trStr = null;
        if(savedInstanceState == null) {
          detailMovie();
        }
    }

    public void detailMovie(){
        scrollView = findViewById(R.id.scroll_view);
        TextView title = findViewById(R.id.title);
        TextView overview = findViewById(R.id.overview);
        TextView release_date = findViewById(R.id.release_date);
        TextView user_rate = findViewById(R.id.user_rate);
        ImageView poster = findViewById(R.id.poster);
        final Intent intentStarted = getIntent();
        Button fav = findViewById(R.id.fav_button);
        rootView = findViewById(R.id.main_view);

        txtView = new TextView(getApplicationContext());
        txtView.setText(getResources().getString(R.string.Trailers));
        txtView.setTextSize(24);
        txtView.setPadding(12, 5, 0, 0);
        txtView.setTextColor(getResources().getColor(R.color.colorGreen));
        rootView.addView(txtView);
        final MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getApplicationContext());
        if (intentStarted.hasExtra(Intent.EXTRA_TEXT)) {
            HashMap<String, String> movieDet = (HashMap<String, String>) intentStarted.getSerializableExtra(Intent.EXTRA_TEXT);

            title.setText(movieDet.get("original_title"));
            movieDet.put("poster", "https://image.tmdb.org/t/p/w500/" + movieDet.get("poster_path"));
            Picasso.with(getApplicationContext()).load(movieDet.get("poster")).placeholder(R.drawable.ic_launcher_background).into(poster);
            overview.setText(movieDet.get("overview"));
            release_date.setText(movieDet.get("release_date"));
            user_rate.setText(movieDet.get("vote_average"));

            String id = movieDet.get("movie_id");
            MyMovieAsyncTask mytask = new MyMovieAsyncTask();
            mytask.execute("videos", id);
            ReviewAsyncTask myReview = new ReviewAsyncTask();
            myReview.execute("reviews", id);
        }
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("MovieIntentActivity", "Inserting ");
                HashMap<String, String> movieDet = (HashMap<String, String>) intentStarted.getSerializableExtra(Intent.EXTRA_TEXT);
                addFavMovies(movieDet);
            }
        });
    }

    private void setOnClick(final TextView imgbutton, final String myVid){
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(myVid)));
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+myVid));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + myVid));

                if (appIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(appIntent);
                }
                else {
                    startActivity(webIntent);
                }

                Log.v("MovieIntentActivity", "Video Playing....");
            }
        });
    }


    private class MyMovieAsyncTask extends AsyncTask<String,Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpRequest trReq = new HttpRequest(strings[0],strings[1]);
            return trReq.getJsonString();
        }

        @Override
        protected void onPostExecute(final String trStr) {
            if(trStr != null){
                MovieDetails myTrList = new MovieDetails(trStr);
                ArrayList<String> myVid = myTrList.getTrailerList();

                for(int idx =0; idx< myVid.size();idx++) {
                    txtView = new TextView(getApplicationContext());
                    int res = idx+1;
                    txtView.setText(String.format("%s%d", getResources().getString(R.string.Trailer), res));
                    txtView.setTextSize(20);
                    txtView.setPadding(50,5,0,0);
                    rootView.addView(txtView);
                    setOnClick(txtView, myVid.get(idx));
                }
            }
            super.onPostExecute(trStr);
        }
    }


    private class ReviewAsyncTask extends AsyncTask<String,Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpRequest trReq = new HttpRequest(strings[0],strings[1]);
            return trReq.getJsonString();
        }

        @Override
        protected void onPostExecute(final String trStr) {
            if(trStr != null){
                MovieDetails myReviewList = new MovieDetails(trStr);
                ArrayList<String> myVid = myReviewList.getReviewList();

                txtView = new TextView(getApplicationContext());
                txtView.setText(getResources().getString(R.string.Reviews));
                txtView.setTextSize(24);
                txtView.setPadding(12,5,0,0);
                txtView.setTextColor(getResources().getColor(R.color.colorGreen));
                rootView.addView(txtView);

                for(int idx =0; idx< myVid.size();idx++) {
                    txtView = new TextView(getApplicationContext());
                    txtView.setText(myVid.get(idx));
                    txtView.setTextSize(16);
                    txtView.setPadding(24,5,0,0);
                    rootView.addView(txtView);
                    //setOnClick(txtView, myVid.get(idx));
                }
            }
            super.onPostExecute(trStr);
        }
    }

    private void  addFavMovies(HashMap<String,String> movieDet){
        ContentValues cv = new ContentValues();
        cv.put(MovieDbContract.MovieDb.COLUMN_MOVIE_ID,movieDet.get("movie_id"));
        cv.put(MovieDbContract.MovieDb.COLUMN_MOVIE_NAME,movieDet.get("original_title"));
        cv.put(MovieDbContract.MovieDb.COLUMN_POSTER_ID,movieDet.get("poster_path"));
        cv.put(MovieDbContract.MovieDb.COLUMN_OVERVIEW,movieDet.get("overview"));
        cv.put(MovieDbContract.MovieDb.COLUMN_RELEASE_DATE,movieDet.get("release_date"));
        cv.put(MovieDbContract.MovieDb.COLUMN_VOTE_AVG,movieDet.get("vote_average"));
        //return mDb.insert(MovieDbContract.MovieDb.TABLE_NAME, null, cv);

        ArrayList<String> mArrayList = new ArrayList<>();
        MoviesDbHelper moviesDbHelper = new MoviesDbHelper(this);
        mDb = moviesDbHelper.getReadableDatabase();
        try {

            Cursor cursor = getContentResolver().query(MovieDbContract.MovieDb.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieDbContract.MovieDb._ID);
            cursor.moveToFirst();
            mArrayList.add(cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieDb.COLUMN_MOVIE_ID)));
            //Log.v("MovieIntentActivity", String.valueOf(cursor.getCount()));
            DatabaseUtils.dumpCursor(cursor);
            while (cursor.moveToNext()) {
                mArrayList.add(cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieDb.COLUMN_MOVIE_ID)));
            }
            //Log.v("MovieIntentActivity", String.valueOf(Arrays.asList(mArrayList)));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //Log.v("MovieIntentActivity", movieDet.get("movie_id"));
        if(! mArrayList.contains(movieDet.get("movie_id"))) {
            Uri uri = getContentResolver().insert(MovieDbContract.MovieDb.CONTENT_URI, cv);
            finish();
        }else {
            Toast.makeText(this,"Already added ",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        scrollView = findViewById(R.id.scroll_view);
        outState.putIntArray("scroll_position",
                new int[]{ scrollView.getScrollX(), scrollView.getScrollY()});
        Log.v("CHK",String.valueOf(scrollView.getScrollX())+" "+String.valueOf(scrollView.getScrollY()));
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        detailMovie();
        final int[] position = savedInstanceState.getIntArray("scroll_position");
        Log.v("CHK",String.valueOf(position[0])+" "+String.valueOf(position[1]));
        if(position != null) {
            scrollView.postDelayed(new Runnable() {
                public void run() {
                    scrollView.scrollTo(position[0], position[1]);
                }
            },500);
        }
    }
}
