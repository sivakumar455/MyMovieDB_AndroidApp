package com.example.android.mymovies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author  Siva Kumar Padala
 * @version 1.0
 * @since   29/03/18
 */

    public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

        private GridView myMovieGrid;
        private final String TOP_RATED = "top_rated";

        private final String TOP_RATED_KEY = "top_rated";

        private static final int MOVIE_FETCH_LOADER = 99;

        private SQLiteDatabase mDb;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            dbFetch(TOP_RATED);
            MoviesDbHelper moviesDbHelper = new MoviesDbHelper(this);
            mDb = moviesDbHelper.getWritableDatabase();
        }

        @Override
        public Loader<String> onCreateLoader(int id, final Bundle args) {
            return  new AsyncTaskLoader<String>(this) {

                String jsonRes;
                @Override
                protected void onStartLoading() {
                    Log.v("MainActivity","onStartLoading");
                    if (args == null)
                    {
                        Log.v("MainActivity","onStartLoading is null");
                        return;
                    }
                    if (jsonRes != null) {
                        deliverResult(jsonRes);
                    } else {
                        forceLoad();
                    }
                    Log.v("MainActivity",args.getString(TOP_RATED_KEY));

                }

                @Override
                public String loadInBackground() {
                    Log.v("MainActivity","loadInBackground");
                    String myRating = args.getString(TOP_RATED_KEY);
                    if(myRating == null || TextUtils.isEmpty(myRating)){
                        return null;
                    }
                    try {
                        Log.v("MainActivity","loadInBackground try ");
                        HttpRequest myMovieReq = new HttpRequest(myRating);
                        return myMovieReq.getJsonString();
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(String data) {
                    jsonRes = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, final String data) {

            Log.v("MainActivity","onLoadFinished");
            finishedJson(data);
        }

        public void finishedJson (final String data){
            MovieDetails myMovieList = new MovieDetails(data);
            ArrayList<String> myArrList = myMovieList.getMovieList();
            MovieAdapter myMovieAdapter  = new MovieAdapter(getApplicationContext(),myArrList);
            myMovieGrid = findViewById(R.id.my_movie_grid);
            myMovieGrid.setAdapter(myMovieAdapter);
            myMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    //Toast.makeText(getApplicationContext(),"Hello",Toast.LENGTH_SHORT).show();
                    HashMap<String,String> myMovie;
                    MovieDetails movieDetails = new MovieDetails(data);
                    myMovie = movieDetails.getMyMovie(position);
                    Intent intent = new Intent(getApplicationContext(), MovieIntentActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT,myMovie);
                    startActivity(intent);
                }
            });
        }

        public void favList(){

            ArrayList<String> mArrayList = new ArrayList<>();

            MoviesDbHelper moviesDbHelper = new MoviesDbHelper(this);
            mDb = moviesDbHelper.getReadableDatabase();
             try {

                 Cursor cursor =  getContentResolver().query(MovieDbContract.MovieDb.CONTENT_URI,
                         null,
                         null,
                         null,
                         MovieDbContract.MovieDb._ID );
                 cursor.moveToFirst();
                 mArrayList.add(cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieDb.COLUMN_POSTER_ID)));
                 //DatabaseUtils.dumpCursor(cursor);
                 while (cursor.moveToNext()) {
                     mArrayList.add(cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieDb.COLUMN_POSTER_ID))); //add the item
                     Log.v("28",cursor.getString(cursor.getColumnIndex(MovieDbContract.MovieDb._ID)));
                 }
             }
             catch (Exception e){
                 e.printStackTrace();
             }

            ArrayList<String> arrli = new ArrayList<>();
            for (int idx=0; idx < mArrayList.size();idx++) {
                arrli.add("https://image.tmdb.org/t/p/w500/"+mArrayList.get(idx));
            }
            MovieAdapter myMovieAdapter  = new MovieAdapter(getApplicationContext(), arrli);
            myMovieGrid = findViewById(R.id.my_movie_grid);
            myMovieGrid.setAdapter(myMovieAdapter);
            myMovieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    //Toast.makeText(getApplicationContext(),"Hello fetch fav ",Toast.LENGTH_SHORT).show();

                    MoviesDbHelper moviesDbHelper = new MoviesDbHelper(getApplicationContext());
                    mDb = moviesDbHelper.getReadableDatabase();
                    int pos = position+1;
                    String query = "SELECT * FROM " + MovieDbContract.MovieDb.TABLE_NAME + " WHERE " + " ROWID " + " = "
                            + pos +" GROUP BY "+ MovieDbContract.MovieDb.COLUMN_MOVIE_ID + " ORDER BY " + MovieDbContract.MovieDb.COLUMN_TIMESTAMP;

                    Cursor res = mDb.rawQuery(query, null);
                    //DatabaseUtils.dumpCursor(res);
                    HashMap<String ,String> map = new HashMap<>();
                    if (res != null ) {
                        if  (res.moveToFirst()) {
                            do {
                                map.put("poster_path",res.getString(res.getColumnIndex(MovieDbContract.MovieDb.COLUMN_POSTER_ID)));
                                map.put("original_title",res.getString(res.getColumnIndex(MovieDbContract.MovieDb.COLUMN_MOVIE_NAME)));
                                map.put("overview", res.getString(res.getColumnIndex(MovieDbContract.MovieDb.COLUMN_OVERVIEW)));
                                map.put("release_date", res.getString(res.getColumnIndex(MovieDbContract.MovieDb.COLUMN_RELEASE_DATE)));
                                map.put("movie_id", res.getString(res.getColumnIndex(MovieDbContract.MovieDb.COLUMN_MOVIE_ID)));
                                map.put("vote_average", res.getString(res.getColumnIndex(MovieDbContract.MovieDb.COLUMN_VOTE_AVG)));
                            }while (res.moveToNext());
                        }
                    }
                    //Log.v("MainActivity", String.valueOf(Arrays.asList(map)));
                    Intent intent = new Intent(getApplicationContext(), MovieIntentActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT,map);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main,menu);
            return true;
        }

        private void dbFetch(String order){

            Bundle myBundle = new Bundle();
            myBundle.putString(TOP_RATED_KEY,order);

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> movieDbLoader = loaderManager.getLoader(MOVIE_FETCH_LOADER);

            if (movieDbLoader == null) {
                Log.v("MainActivity","loader is null");
                loaderManager.initLoader(MOVIE_FETCH_LOADER, myBundle, this);
            } else {
                Log.v("MainActivity","loader not null");
                loaderManager.restartLoader(MOVIE_FETCH_LOADER, myBundle, this);
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            myMovieGrid = findViewById(R.id.my_movie_grid);
            switch(item.getItemId()){
                case R.id.top_rated_movie:
                    //Toast.makeText(getApplicationContext(),"Top Rated",Toast.LENGTH_SHORT).show();
                    dbFetch(TOP_RATED);
                    return true;
                case  R.id.popular_movie:
                    //Toast.makeText(getApplicationContext(),"Popular",Toast.LENGTH_SHORT).show();
                    String POPULAR = "popular";
                    Log.v("MainActivity","Popular");
                    dbFetch(POPULAR);
                    return true;
                case R.id.favourite_movie:
                    Log.v("MainActivity","Favourite");
                    favList();
                    return true;
                default:
                    Toast.makeText(getApplicationContext(),"Default",Toast.LENGTH_SHORT).show();
            }
            return super.onOptionsItemSelected(item);
        }
    }
