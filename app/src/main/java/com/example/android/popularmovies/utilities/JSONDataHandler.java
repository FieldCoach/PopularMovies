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

    private static final String POSTER_PATH = "poster_path";
    private static final String RESULTS = "results";

    /**
     * Makes the JSON data easier to work with by taking the data from the String, creating JSONObjects,
     * then putting those objects into an ArrayList to later pass to RecyclerView.Adapter
     * @param movieRequestResults String containing JSON data
     * @return ArrayList of movie JSONObjects
     * @throws JSONException
     */
    public static ArrayList<JSONObject> getMovieJSONObjectsArray(String movieRequestResults) throws JSONException{

        JSONObject movieResultsJSONObject = new JSONObject(movieRequestResults);
        JSONArray movieResultsJSONArray = movieResultsJSONObject.getJSONArray(RESULTS);

        ArrayList<JSONObject> movieJSONObjectsArray = new ArrayList<>();

        for (int i = 0; i < movieResultsJSONArray.length(); i++) {
            JSONObject movieObject = movieResultsJSONArray.getJSONObject(i);

            movieJSONObjectsArray.add(movieObject);
        }
        return movieJSONObjectsArray;
    }

    /**
     * Creates an ArrayList of Uri's of the poster locations to later pass to RecyclerView.Adapter
     * @param movieJSONObjectsArray the ArrayList of movie JSONObjects
     * @return ArrayList of Uri's of the poster locations
     */
    public static ArrayList<Uri> getPosterLocationsArray(ArrayList<JSONObject> movieJSONObjectsArray){

        ArrayList<Uri> posterLocationsArray = new ArrayList<>();
        for (int i = 0; i < movieJSONObjectsArray.size(); i++) {
            String posterLocationString = "";
            try {
                //substring is used to prevent error caused by forward slash at he beginning of the path
                posterLocationString = movieJSONObjectsArray.get(i).getString(POSTER_PATH).substring(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Uri moviePosterUri = NetworkUtils.buildImageUri(posterLocationString);

            posterLocationsArray.add(moviePosterUri);
        }
        return posterLocationsArray;
    }

    /**
     * Convenience method for MovieDetailsAcitvity to catch any JSONExceptions when using moviewObject.getString()
     * @param movieObject the current movieObject of the MovieDetailsActivity
     * @param key the desired detail to retrieve for the movieObject
     * @return the details requested
     */
    public static String getDetailsString(JSONObject movieObject, String key){
        String detailsString = "";
        try {
            detailsString = movieObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return detailsString;
    }

}