package com.example.android.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbService {

    @GET("{sort_by}")
    Call<Movies> getSortedMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey, @Query("page") String page);
}
