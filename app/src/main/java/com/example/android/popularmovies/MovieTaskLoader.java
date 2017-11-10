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
 */

public class MovieTaskLoader extends AsyncTaskLoader<String> {

    public static final String TAG = MovieTaskLoader.class.getSimpleName();

    private String movieRequestUrl;
    private Bundle bundle;

    public MovieTaskLoader(@NonNull Context context, String movieRequestUrl, Bundle bundle) {
        super(context);
        this.movieRequestUrl = movieRequestUrl;
        this.bundle = bundle;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        // TODO: 11/9/2017 onStartLoading() - handle loading progressbar
    }

    @Nullable
    @Override
    public String loadInBackground() {
        String movieRequestUrlString = bundle.getString(movieRequestUrl);

        if (movieRequestUrlString == null || movieRequestUrlString.equals("")) return null;

        try {
            URL movieRequestUrl = new URL(movieRequestUrlString);
            String jsonResultString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            Log.d(TAG, "loadInBackground() returned: " + jsonResultString);
            return jsonResultString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected boolean onCancelLoad() {
        cancelLoadInBackground();
        Log.d(TAG, "onCancelLoad: was called");
        return super.onCancelLoad();
    }
}

