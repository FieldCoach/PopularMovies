package com.example.android.popularmovies.viewmodel;

import android.app.Application;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieDao;
import com.example.android.popularmovies.data.MovieDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MovieListViewModel extends AndroidViewModel {
    private MovieDao movieDao;

    public MovieListViewModel(@NonNull Application application) {
        super(application);
        movieDao = MovieDatabase.getDatabase(application).movieDao();
    }

    public LiveData<List<Movie>> getMovies() {
        return movieDao.getMovies();
    }
}
