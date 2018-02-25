package com.example.android.mymovies;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author  Siva Kumar Padala
 * @version 1.0
 * @since   04/01/18
 */

class MovieDetails {
    private final String jsonMovieObj;

    public MovieDetails(String movieObj ){
        jsonMovieObj = movieObj;
    }

    public ArrayList<String> getMovieList(){
        String id;
        ArrayList<String> arrli = new ArrayList<>();
        try {
            JSONObject objectResults = new JSONObject(jsonMovieObj);
            JSONArray resultArray = objectResults.getJSONArray("results");
            for (int idx=0; idx < resultArray.length();idx++) {
                JSONObject newObj = resultArray.getJSONObject(idx);
                id = newObj.getString("poster_path");
                arrli.add("https://image.tmdb.org/t/p/w500/"+id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return arrli;
    }
    public HashMap<String,String> getMyMovie(int position){
        HashMap<String ,String> map = null;
        try {
            JSONObject objectResults = new JSONObject(jsonMovieObj);
            JSONArray resultArray = objectResults.getJSONArray("results");
            JSONObject newObj = resultArray.getJSONObject(position);
            String poster_path = newObj.getString("poster_path");
            String vote_average = newObj.getString("vote_average");
            String original_title = newObj.getString("original_title");
            String overview = newObj.getString("overview");
            String release_date = newObj.getString("release_date");

            map = new HashMap<>();
            map.put("poster_path",poster_path);
            map.put("vote_average",vote_average);
            map.put("original_title",original_title);
            map.put("overview",overview);
            map.put("release_date",release_date);
            map.put("movie_id", String.valueOf(position));
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
}
