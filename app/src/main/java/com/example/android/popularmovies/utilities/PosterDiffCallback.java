package com.example.android.popularmovies.utilities;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.android.popularmovies.data.Movie;

import java.util.List;

public class PosterDiffCallback extends DiffUtil.Callback {

    private List<Movie> oldMovies;
    private List<Movie> newMovies;

    public PosterDiffCallback(List<Movie> oldMovies, List<Movie> newMovies) {
        this.oldMovies = oldMovies;
        this.newMovies = newMovies;
    }


    @Override
    public int getOldListSize() {
        return oldMovies.size();
    }

    @Override
    public int getNewListSize() {
        return newMovies.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMovies.get(oldItemPosition).getId() == newMovies.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Movie oldMovie = oldMovies.get(oldItemPosition);
        Movie newMovie = newMovies.get(newItemPosition);

        return oldMovie.getTitle()
                .equals(newMovie.getTitle());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
