package com.example.android.mymovies;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author  Siva Kumar Padala
 * @version 1.0
 * @since   04/01/18
 */

class HttpRequest {
    private final String httpUrl;
    private final String sortOrder;
    private final String movId;

    private final static String MOVIE_BASE_URL =
            "http://api.themoviedb.org/3/movie";
    private final static String API_KEY = "api_key";
    private final static String api_pass = "PASTE_YOUR_KEY";

    public HttpRequest(String sort){
        sortOrder = sort;
        httpUrl = buildUrl();
        movId = null;
    }

    public HttpRequest(String sort, String id){
        sortOrder = sort;
        movId = id;
        httpUrl = buildVidReviewUrl(movId);
    }

    private String buildUrl() {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(API_KEY, api_pass)
                .build();
        String uriStr = builtUri.toString();
        Log.v("HTTP",uriStr);
        return uriStr;
    }

    private String buildVidReviewUrl(String id) {
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(sortOrder)
                .appendQueryParameter(API_KEY, api_pass)
                .build();
        String uriStr = builtUri.toString();
        Log.v("HTTP check",uriStr);
        return uriStr;
    }

    public String getJsonString(){
        HttpURLConnection urlConnection = null;
        String res =null;

        try {
            URL url = new URL(httpUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(isw);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line= reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            res = sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return  res;
    }
}
