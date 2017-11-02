package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.ApiKeyFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * Created by AaronC on 7/27/2017.
 */

public final class NetworkUtils {

    private final static String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie";

    private final static String API_KEY_STRING = "api_key";
    private static final String PAGE = "page";

    private static final String REVIEWS = "reviews";
    private static final String APPEND_STRING = "append_to_response";

    private final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String IMAGE_SIZE = "w342";

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Builds the url used to fetch movies from the API of themoviedb.org
     * @param sortOrder must be either popular or top_rated
     * @return the built url
     */
    public static URL buildMovieDbUrl(String sortOrder, String page) {
        //Build a Uri with the base URL and the query parameters
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(sortOrder.toLowerCase())
                .appendQueryParameter(API_KEY_STRING, ApiKeyFile.API_KEY)
                .appendQueryParameter(PAGE, page)
                .build();

        URL url = null;
        try {
            //Construct a new URL by passing it a String representation of the Uri
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "buildMovieDbUrl() returned: " + url);
        return url;
    }

    /**
     * Builds the url used to show the poster of a movie
     * @param imageToDisplay the file name of the image to be shown
     * @return a String representation of the builtUri
     */
    //Returning a String because its usage is more versatile than Uri in usage in this project
    public static String buildImageUriString(String imageToDisplay) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE)
                .appendPath(imageToDisplay)
                .build();

        return builtUri.toString();
    }

    public static URL buildReviewsDbUrl(String movieId) {
        //Build a Uri with the base URL and the path endpoints
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(API_KEY_STRING, ApiKeyFile.API_KEY)
                .appendQueryParameter(APPEND_STRING, REVIEWS)
                .build();

        URL url = null;
        try {
            //Construct a new URL
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "buildReviewsDbUrl() returned: " + url);
        return url;
    }

    /**
     * Retrieves the JSON data from the internet using a GET request from the url
     * @param url the url to fetch movies from the themoviedb.org API
     * @return JSON data from API
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);

            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
                urlConnection.disconnect();
        }
    }

}
