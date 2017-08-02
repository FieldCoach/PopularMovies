package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

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

    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    final static String QUERY_PARAMETER = "api_key";

    final static String IMAGE_BASE_URL = " http://image.tmdb.org/t/p/";
    final static String IMAGE_SIZE = "w185";

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Builds the url used to fetch popular movies from the API of themoviedb.org
     * @param sortOrder must be either popular or top_rated
     * @param apiKey request this from the user to prevent issues with GitHub
     * @return the built url
     */
    public static URL buildMovieDbUrl(String sortOrder, String apiKey) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(QUERY_PARAMETER, apiKey)
                .build();

        URL url = null;
        try {
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
     * @return
     */
    public static URL buildImageUrl(String imageToDisplay) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE)
                .appendPath(imageToDisplay)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "buildImageUrl() returned: " + url);
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
