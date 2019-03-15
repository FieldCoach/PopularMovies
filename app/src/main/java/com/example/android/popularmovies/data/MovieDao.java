package com.example.android.popularmovies.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM Movie")
    LiveData<List<Movie>> getMovies();

    @Query("SELECT * FROM Movie WHERE movieId = :id")
    LiveData<Movie> getMovie(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    void insertAllMovies(List<Movie> movies);

    @Delete
    void deleteMovie(Movie movie);

    @Delete
    void deleteAllMovies(List<Movie> movies);
}
