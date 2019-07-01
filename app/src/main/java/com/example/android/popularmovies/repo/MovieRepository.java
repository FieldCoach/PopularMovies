/*
 * <!--
 *  Copyright (C) 2016 The Android Open Source Project
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 * -->
 */

package com.example.android.popularmovies.repo;

import android.util.Log;

import com.example.android.popularmovies.ApiKeyFile;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.utilities.MovieDbService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieRepository {
    private static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    private String pageNumber = "";
    private MutableLiveData<List<Movie>> serverMovies;

    public MovieRepository() {
        serverMovies = new MutableLiveData<>();
    }

    public LiveData<List<Movie>> getMoviesFromServer() {
        // Build Http Client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // Build Retrofit Object with Base URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        // Create the Service Object containing the @GET call
        MovieDbService service = retrofit.create(MovieDbService.class);
        // Create the Call by calling the @GET method from the Service
        Call<Movies> call = service.getSortedMovies(
                "popular",
                ApiKeyFile.MOVIE_DB_API_KEY,
                pageNumber);
        // Use the method enqueue from the Call to act upon onResponse and onFailure
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                Movies movies = response.body();
                if(movies != null) {
                    serverMovies.setValue(movies.getMovies());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call,@NonNull Throwable t) {
                Log.d("MovieRepository", "onFailure: " + t.getMessage());
            }
        });
        return serverMovies;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = String.valueOf(pageNumber);
    }

    public void loadMoviesFromServer(int pageNumber) {
        setPageNumber(pageNumber);
        getMoviesFromServer();
    }
}
