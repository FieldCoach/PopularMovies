package com.example.android.popularmovies.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.repo.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    private MovieRepository repo;

    public MovieListViewModel() {
        this.repo = new MovieRepository();
    }

    public LiveData<List<Movie>> getMovies() {
        return repo.getMoviesFromServer();
    }
}
