package com.example.android.popularmovies.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.repo.MovieRepository;

import java.util.List;

public class MovieListViewModel extends ViewModel {
    private static final int PAGE_NUMBER_INCREMENT = 1;
    private MovieRepository repo;
    private int pageNumber = 1;

    public MovieListViewModel() {
        this.repo = new MovieRepository();
        repo.setPageNumber(pageNumber);
    }

    public LiveData<List<Movie>> getMovies() {
        return repo.getMoviesFromServer();
    }

    public void loadMovies() {
        pageNumber += PAGE_NUMBER_INCREMENT;
        repo.loadMoviesFromServer(pageNumber);
    }
}
