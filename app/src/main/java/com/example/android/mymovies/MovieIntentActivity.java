package com.example.android.mymovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class MovieIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_intent);

        TextView title = findViewById(R.id.title);
        TextView overview = findViewById(R.id.overview);
        TextView release_date = findViewById(R.id.release_date);
        TextView user_rate = findViewById(R.id.user_rate);
        ImageView poster = findViewById(R.id.poster);
        Intent intentStarted = getIntent();

        if (intentStarted.hasExtra(Intent.EXTRA_TEXT)){
            HashMap<String,String> movieDet = (HashMap<String, String>) intentStarted.getSerializableExtra(Intent.EXTRA_TEXT);

            title.setText(movieDet.get("original_title"));
            movieDet.put("poster","https://image.tmdb.org/t/p/w500/"+movieDet.get("poster_path"));
            Picasso.with(getApplicationContext()).load(movieDet.get("poster")).into(poster);
            overview.setText(movieDet.get("overview"));
            release_date.setText(movieDet.get("release_date"));
            user_rate.setText(movieDet.get("vote_average"));
        }
    }
}
