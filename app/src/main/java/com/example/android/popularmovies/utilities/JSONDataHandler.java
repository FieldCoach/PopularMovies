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

    public static ArrayList<JSONObject> getMovieObjectsArray(String movieRequestResults) throws JSONException{

        final String RESULTS = "results";

        JSONObject movieResultsJson = new JSONObject(movieRequestResults);
        JSONArray resultsArray = movieResultsJson.getJSONArray(RESULTS);

        ArrayList<JSONObject> moviePostersArray = new ArrayList<>();

        for (int i = 0; i < resultsArray.length(); i++) {
            JSONObject movieObject = resultsArray.getJSONObject(i);

            moviePostersArray.add(movieObject);
        }
        return moviePostersArray;
    }

    public static ArrayList<Uri> getPosterLocationsArray(ArrayList<JSONObject> movieObjectsArray){

        ArrayList<Uri> posterLocationsArray = new ArrayList<>();
        for (int i = 0; i < movieObjectsArray.size(); i++) {
            String posterLocation = "";
            try {
                posterLocation = movieObjectsArray.get(i).getString("poster_path").substring(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Uri moviePosterUri = NetworkUtils.buildImageUri(posterLocation);

            posterLocationsArray.add(moviePosterUri);
        }
        return posterLocationsArray;
    }

    public static String getDetails(JSONObject movieObject, String key){
        String details = "";
        try {
            details = movieObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }

}

/*
vote_average
title = decimal
title = string
poster_path = string
backdrop_path = string
overview = string
release_date = string


 */