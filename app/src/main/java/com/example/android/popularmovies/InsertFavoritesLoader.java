package com.example.android.popularmovies;

import android.content.Context;
import android.os.Bundle;

import androidx.loader.content.AsyncTaskLoader;

import com.example.android.popularmovies.data.Movie;

/**
 * Created by ioutd on 11/16/2017.
 *
 * InsertFavoritesLoader is deprecated.
 */
@Deprecated
class InsertFavoritesLoader extends AsyncTaskLoader {

    private Movie movie;

    public InsertFavoritesLoader(Context context, Bundle bundle, String movieKey) {
        super(context);
        movie = bundle.getParcelable(movieKey);
    }

    @Override
    public Object loadInBackground() {
        /**
        ContentValues values = new ContentValues();

        //Add the movie details to the database
        values.put(MovieContract.MovieEntry.COLUMN_POSTER, movie.getPosterPath());
        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP, movie.getBackdropPath());
        values.put(MovieContract.MovieEntry.COLUMN_PLOT, movie.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getVoteAverage());
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE, movie.getReleaseDate());
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());

        getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
         */
        return null;
    }

}
