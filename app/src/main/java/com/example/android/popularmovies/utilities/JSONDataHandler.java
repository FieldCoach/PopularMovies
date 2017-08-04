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

    public static ArrayList<JSONObject> getMovieJSONObjectsArray(String movieRequestResults) throws JSONException{

        final String RESULTS = "results";

        JSONObject movieResultsJSONObject = new JSONObject(movieRequestResults);
        JSONArray movieResultsJSONArray = movieResultsJSONObject.getJSONArray(RESULTS);

        ArrayList<JSONObject> movieJSONObjectsArray = new ArrayList<>();

        for (int i = 0; i < movieResultsJSONArray.length(); i++) {
            JSONObject movieObject = movieResultsJSONArray.getJSONObject(i);

            movieJSONObjectsArray.add(movieObject);
        }
        return movieJSONObjectsArray;
    }

    public static ArrayList<Uri> getPosterLocationsArray(ArrayList<JSONObject> movieJSONObjectsArray){

        ArrayList<Uri> posterLocationsArray = new ArrayList<>();
        for (int i = 0; i < movieJSONObjectsArray.size(); i++) {
            String posterLocationString = "";
            try {
                //substring is used to prevent error caused by forward slash at he beginning of the path
                posterLocationString = movieJSONObjectsArray.get(i).getString("poster_path").substring(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Uri moviePosterUri = NetworkUtils.buildImageUri(posterLocationString);

            posterLocationsArray.add(moviePosterUri);
        }
        return posterLocationsArray;
    }

    public static String getDetails(JSONObject movieObject, String key){
        String detailsString = "";
        try {
            detailsString = movieObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detailsString;
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