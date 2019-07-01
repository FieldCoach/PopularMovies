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

package com.example.android.popularmovies.ui.list;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.ApiKeyFile;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.ui.PosterAdapter;
import com.example.android.popularmovies.ui.details.MovieDetailsFragment;
import com.example.android.popularmovies.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies.utilities.MovieDbService;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.viewmodel.MovieListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by androidpirate.
 */
public class MovieListFragment extends Fragment
    implements PosterAdapter.PosterAdapterClickListener {

    private static final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String FAVORITES = "favorites";

    private List<Movie> moviesArrayList = new ArrayList<>();
    private String sortBySelectionString = "popular";
    private String mPage = "1";
    private RecyclerView recyclerView;
    private PosterAdapter adapter;

    public MovieListFragment() {
        // Required empty public constructor
    }

    public static MovieListFragment newInstance() {
        return new MovieListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                        R.layout.fragment_movie_list,
                        container,
                        false);
        recyclerView = view.findViewById(R.id.rv_list);
        // Setup GridLayoutManager
        setLayoutManager();
        adapter = new PosterAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        //if(isConnectedToNetwork()) {
            // getMoviesFromServer();
        // }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MovieListViewModel viewModel = ViewModelProviders.of(this)
                .get(MovieListViewModel.class);
        viewModel.getMovies()
                .observe(this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(List<Movie> movies) {
                        moviesArrayList.addAll(movies);
                        adapter.updateMoviesList(moviesArrayList);
                    }
                });
    }

    /**
     * Uses retrofit to get movies from MovieDB server.
     */
    private void getMoviesFromServer() {
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
                mPage);
        Log.d("MainActivity", "getMovies: " + call.toString());
        Log.d("MainActivity", "getMovies: " + mPage);
        // Use the method enqueue from the Call to act upon onResponse and onFailure
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                Movies movies = response.body();
                if(movies != null) {
                    if (moviesArrayList == null) {
                        // Get an ArrayList containing the Movies
                        moviesArrayList = movies.getMovies();
                    } else {
                        // Get an ArrayList containing more Movies and add them to the existing Movies
                        List<Movie> moreMovies = movies.getMovies();
                        moviesArrayList.addAll(moreMovies);
                    }
                }
                adapter.updateMoviesList(moviesArrayList);
                Log.d("MainActivity", "onResponse: " + mPage);
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call,@NonNull Throwable t) {
                Log.d("MainActivity", "onFailure: " + t.getMessage());
            }
        });
    }

    /**
     * Calculates the number of columns and sets a grid layout manager for recyclerView.
     */
    private void setLayoutManager() {
        int noOfColumns = calculateNoOfColumns();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), noOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        // Setup endless scrolling
        setupEndlessScrolling(layoutManager);
    }

    /**
     * Calculates the number of columns for the GridLayoutManager
     * @return the number of columns
     */
    private int calculateNoOfColumns() {
        DisplayMetrics displayMetrics = Objects.requireNonNull(getContext())
                .getResources()
                .getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        return (int) (dpWidth / scalingFactor);
    }

    /**
     * Loads more pages to recycler view on the fly.
     * @param layoutManager recyclerView layout manager
     */
    private void setupEndlessScrolling(GridLayoutManager layoutManager) {
        //Construct a new Endless Scroll Listener and pass it the GridLayoutManager
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Use field page instead of the parameter for this method
                //Using the parameter page caused the same page of result to be loaded multiple times
                if (!sortBySelectionString.equals(FAVORITES)){
                    setPageNumber(Integer.valueOf(mPage) + 1);
                    getMoviesFromServer();
                }
            }
        };
        //Add an OnScrollListener to the RecyclerView and pass it the Endless Scroll Listener
        recyclerView.addOnScrollListener(scrollListener);
    }

    /**
     * Sets which mPage for results to display
     * @param mPage the page to display results from
     */
        private void setPageNumber(int mPage) {
        this.mPage = String.valueOf(mPage);
    }

    /**
     * Checks for network connection status.
     */
    private boolean isConnectedToNetwork() {
        return NetworkUtils.isOnline(Objects.requireNonNull(getContext()));
    }

    @Override
    public void onPosterClick(int movieId) {
        navigateToDetail(movieId);
    }

    private void navigateToDetail(int movieId) {
        Bundle args = new Bundle();
        args.putInt(MovieDetailsFragment.ARG_MOVIE_ID, movieId);
        Navigation.findNavController(Objects.requireNonNull(getActivity()),
                                    R.id.nav_host_fragment)
                .navigate(R.id.action_list_to_detail, args);
    }
}
