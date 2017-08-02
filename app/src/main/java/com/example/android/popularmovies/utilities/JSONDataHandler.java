package com.example.android.popularmovies.utilities;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AaronC on 8/2/2017.
 */

public class JSONDataHandler {

    public static ArrayList<Uri> getMoviePosters(String movieRequestResults) throws JSONException{

        final String POSTER_PATH = "poster_path";
        final String RESULTS = "results";

        JSONObject movieResultsJson = new JSONObject(movieRequestResults);
        JSONArray resultsArray = movieResultsJson.getJSONArray(RESULTS);

        ArrayList<Uri> moviePostersArray = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject movieObject = resultsArray.getJSONObject(i);

            //Use substring because "/" is converted to %2f and causes issues
            String poster = movieObject.getString(POSTER_PATH).substring(1);
            Uri moviePosterUrl = NetworkUtils.buildImageUri(poster);

            moviePostersArray.add(moviePosterUrl);
        }
        return moviePostersArray;
    }

}
