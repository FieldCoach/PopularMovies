package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by ioutd on 11/14/2017.
 */
@Deprecated
public class DetailsLoader extends AsyncTaskLoader<String> {
    private Bundle bundle;
    private String detailsRequestUrl;

    DetailsLoader(@NonNull Context context, Bundle bundle, String detailsRequestUrl) {
        super(context);
        this.bundle = bundle;
        this.detailsRequestUrl = detailsRequestUrl;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    /**
     * Loads the trailers and reviews from the request URL
     * @return JSON results
     */
    @Nullable
    @Override
    public String loadInBackground() {
        /**
        //Get the details request url
        String detailsRequestString = bundle.getString(detailsRequestUrl);

        //If the url is null, there is nothing to load
        if (detailsRequestString == null || detailsRequestString.equals("")) return null;

        //Load the JSON response from the url
        try {
            URL detailsRequestUrl = new URL(detailsRequestString);
            String jsonResultString = NetworkUtils.getResponseFromHttpUrl(detailsRequestUrl);

            return jsonResultString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }*/
        // TODO 5: loadInBackground returns null
        return null;
    }
}
