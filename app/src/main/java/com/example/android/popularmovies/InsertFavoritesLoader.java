package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.popularmovies.data.MovieContract;

/**
 * Created by ioutd on 11/16/2017.
 */

class InsertFavoritesLoader extends AsyncTaskLoader {

    private Movie movie;

    public InsertFavoritesLoader(Context context, Bundle bundle, String movieKey) {
        super(context);
        movie = bundle.getParcelable(movieKey);
    }

    @Override
    public Object loadInBackground() {
        ContentValues values = new ContentValues();

        //Add the movie details to the database
        values.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPosterLocationUriString());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP, movie.getBackdropLocationUriString());
        values.put(MovieContract.MovieEntry.COLUMN_PLOT, movie.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE, movie.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());

        getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

        return null;
    }

}
