package com.example.android.mymovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private GridView myMovieGrid;
    private final String TOP_RATED = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyMovieAsyncTask myStr = new MyMovieAsyncTask();
        myStr.execute(TOP_RATED);
    }

    private class MyMovieAsyncTask extends AsyncTask<String,Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            HttpRequest myMovieReq = new HttpRequest(strings[0]);
            return myMovieReq.getJsonString();
        }

        @Override
        protected void onPostExecute(final String str) {

            MovieDetails myMovieList = new MovieDetails(str);
            ArrayList<String> myArrList = myMovieList.getMovieList();
            MovieAdapter myMovieAdapter  = new MovieAdapter(getApplicationContext(),myArrList);
            myMovieGrid = findViewById(R.id.my_movie_grid);
            myMovieGrid.setAdapter(myMovieAdapter);
            myMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    //Toast.makeText(getApplicationContext(),"Hello",Toast.LENGTH_SHORT).show();
                    HashMap<String,String> myMovie;
                    MovieDetails movieDetails = new MovieDetails(str);
                    myMovie = movieDetails.getMyMovie(position);
                    Intent intent = new Intent(getApplicationContext(), MovieIntentActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT,myMovie);
                    startActivity(intent);
                }
            });
            super.onPostExecute(str);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        myMovieGrid = findViewById(R.id.my_movie_grid);
        MyMovieAsyncTask myStr = new MyMovieAsyncTask();

        switch(item.getItemId()){
            case R.id.top_rated_movie:
                //Toast.makeText(getApplicationContext(),"Top Rated",Toast.LENGTH_SHORT).show();
                myStr.execute(TOP_RATED);
                return true;
            case  R.id.popular_movie:
                //Toast.makeText(getApplicationContext(),"Popular",Toast.LENGTH_SHORT).show();
                String POPULAR = "popular";
                myStr.execute(POPULAR);
                return true;
            default:
                Toast.makeText(getApplicationContext(),"Default",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
