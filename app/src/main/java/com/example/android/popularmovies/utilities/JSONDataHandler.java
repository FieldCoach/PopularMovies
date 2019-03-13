package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AaronC on 8/2/2017.
 */

public class JSONDataHandler {

    private static final String TAG = JSONDataHandler.class.getSimpleName();

    private static final String ID = "id";
    private static final String RESULTS = "results";
    private static final String POSTER_PATH = "poster_path";
    private static final String BACKDROP_PATH = "backdrop_path";
    private static final String TITLE = "title";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";

    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";

    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String KEY = "key";

    /**
     * Retrieves details from the JSON String and stores them as fields within multiple Movie Objects.
     * @param jsonResultString String representation of JSON data
     * @return ArrayList of Movie Objects
     * @throws JSONException
     */
    public static ArrayList<Movie> getMovieArrayList(String jsonResultString) throws JSONException{

        //Convert the JSON String into a JSON Object. Then get a JSON Array from the JSON Object
        JSONObject jsonObject = new JSONObject(jsonResultString);
        JSONArray jsonArray = jsonObject.getJSONArray(RESULTS);

        //Create an ArrayList to hold all of the Movie Objects
        ArrayList<Movie> moviesArray = new ArrayList<>();

        //Iterate over the JSON Array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject JSONMovieObject = jsonArray.getJSONObject(i);

            //Get a String representation of the poster path Uri
            String posterPath = JSONMovieObject.getString(POSTER_PATH).substring(1);
            String moviePosterUriString = NetworkUtils.buildImageUriString(posterPath);

            //Get a String representation of the backdrop path Uri
            String backdropPath = JSONMovieObject.getString(BACKDROP_PATH).substring(1);
            String backdropUriString = NetworkUtils.buildImageUriString(backdropPath);

            //Store all of the details as fields within a Movie Object
            Movie movie = new Movie.Builder()
                    .movieId(JSONMovieObject.getString(ID))
                    .posterLocationUriString(moviePosterUriString)
                    .backdropLocationUriString(backdropUriString)
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

    public static ArrayList<Review> getReviewArrayList(String jsonResultString) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonResultString);

        JSONObject JSONReviews = jsonObject.getJSONObject(REVIEWS);
        JSONArray JSONResults = JSONReviews.getJSONArray(RESULTS);

        ArrayList<Review> reviewArray = new ArrayList<>();

        for (int i = 0; i < JSONResults.length(); i++){
            JSONObject JSONReviewObject = JSONResults.getJSONObject(i);

            String author = JSONReviewObject.getString(AUTHOR);
            String content = JSONReviewObject.getString(CONTENT);

            Review review = new Review(author, content);

            reviewArray.add(review);
        }

        return reviewArray;
    }

    public static ArrayList<String> getTrailerArrayList(String resultsString) throws JSONException {
        JSONObject jsonObject = new JSONObject(resultsString);


        JSONObject JSONTrailers = jsonObject.getJSONObject(VIDEOS);
        JSONArray JSONResults = JSONTrailers.getJSONArray(RESULTS);


        ArrayList<String> trailerArray = new ArrayList<>();

        for (int i = 0; i < JSONResults.length(); i++){
            JSONObject JSONTrailerObject = JSONResults.getJSONObject(i);

            trailerArray.add(JSONTrailerObject.getString(KEY));
        }
        return trailerArray;
    }
}