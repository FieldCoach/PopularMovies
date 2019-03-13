package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by ioutd on 11/10/2017.
 *
 * MoviesLoader is deprecated.
 */
@Deprecated
public class MoviesLoader extends AsyncTaskLoader<String> {
    /**
    private static final String TAG = MoviesLoader.class.getSimpleName();

    private String movieRequestUrl;
    private Bundle bundle; */

    MoviesLoader(@NonNull Context context, String movieRequestUrl, Bundle bundle) {
        super(context);
        // this.movieRequestUrl = movieRequestUrl;
        // this.bundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    /**
     * Loads the movie details from its URL
     * @return JSON results
     */
    @Nullable
    @Override
    public String loadInBackground() {
        /**
        //Get the movie's url
        String movieRequestUrlString = bundle.getString(movieRequestUrl);

        //If the url is empty, there is nothing to load
        if (movieRequestUrlString == null || movieRequestUrlString.equals("")) return null;

        //Load the JSON results from the url
        try {
            URL movieRequestUrl = new URL(movieRequestUrlString);
            String jsonResultString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            Log.d(TAG, "loadInBackground() returned: " + jsonResultString);
            return jsonResultString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
        // TODO 6: loadInBackground() returns null
        return null;
    }

    /**
     * Prevents the movie from being loaded in the background
     * @return call to the super method
     */
    @Override
    protected boolean onCancelLoad() {
        /**
        cancelLoadInBackground();
        return super.onCancelLoad(); */
        // TODO 7: onCancelLoad() returns false
        return false;
    }
}

