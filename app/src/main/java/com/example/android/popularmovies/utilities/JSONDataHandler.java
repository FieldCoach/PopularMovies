package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AaronC on 8/2/2017.
 */

public class JSONDataHandler {

    private static final String RESULTS = "results";
    private static final String POSTER_PATH = "poster_path";
    private static final String TITLE = "title";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";

    /**
     * Retrieves details from the JSON String and stores them as fields within multiple Movie Objects.
     * @param movieRequestResults String representation of JSON data
     * @return ArrayList of Movie Objects
     * @throws JSONException
     */
    public static ArrayList<Movie> getMovieArrayList(String movieRequestResults) throws JSONException{

        //Convert the JSON String into a JSON Object. Then get a JSON Array from the JSON Object
        JSONObject jsonObject = new JSONObject(movieRequestResults);
        JSONArray jsonArray = jsonObject.getJSONArray(RESULTS);

        //Create an ArrayList to hold all of the Movie Objects
        ArrayList<Movie> moviesArray = new ArrayList<>();

        //Iterate over the JSON Array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject JSONMovieObject = jsonArray.getJSONObject(i);

            //Get a String representation of the poster path Uri
            String posterPath = JSONMovieObject.getString(POSTER_PATH).substring(1);
            String moviePosterUriString = NetworkUtils.buildImageUriString(posterPath);

            //Store all of the details as fields within a Movie Object
            Movie movie = new Movie.Builder()
                    .posterLocationUriString(moviePosterUriString)
                    .title(JSONMovieObject.getString(TITLE))
                    .voteAverage(JSONMovieObject.getString(VOTE_AVERAGE))
                    .overview(JSONMovieObject.getString(OVERVIEW))
                    .releaseDate(JSONMovieObject.getString(RELEASE_DATE))
                    .build();

            //Add the Movie Object to the ArrayList
            moviesArray.add(movie);
        }
        return moviesArray;
    }

}