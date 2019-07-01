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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.ui.PosterAdapter;
import com.example.android.popularmovies.ui.details.MovieDetailsFragment;
import com.example.android.popularmovies.utilities.EndlessRecyclerViewScrollListener;
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

    private static final String FAVORITES = "favorites";

    private List<Movie> moviesArrayList = new ArrayList<>();
    private String sortBySelectionString = "popular";
    private RecyclerView recyclerView;
    private PosterAdapter adapter;
    private MovieListViewModel viewModel;

    public MovieListFragment() {
        // Required empty public constructor
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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this)
                .get(MovieListViewModel.class);
        if(isConnectedToNetwork()) {
            viewModel.getMovies()
                    .observe(this, movies -> {
                        moviesArrayList.addAll(movies);
                        adapter.updateMoviesList(moviesArrayList);
                    });
        }
    }

    @Override
    public void onPosterClick(int movieId) {
        navigateToDetail(movieId);
    }

    /**
     * Calculates the number of columns and sets a grid layout manager for recyclerView.
     */
    private void setLayoutManager() {
        int noOfColumns = calculateNoOfColumns();
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), noOfColumns);
        // Setup endless scrolling
        setupEndlessScrolling(layoutManager);
        recyclerView.setLayoutManager(layoutManager);
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
                    viewModel.loadMovies();
                }
            }
        };
        //Add an OnScrollListener to the RecyclerView and pass it the Endless Scroll Listener
        recyclerView.addOnScrollListener(scrollListener);
    }

    /**
     * Checks for network connection status.
     */
    private boolean isConnectedToNetwork() {
        return NetworkUtils.isOnline(Objects.requireNonNull(getContext()));
    }

    private void navigateToDetail(int movieId) {
        Bundle args = new Bundle();
        args.putInt(MovieDetailsFragment.ARG_MOVIE_ID, movieId);
        Navigation.findNavController(
                Objects.requireNonNull(getActivity()),
                R.id.nav_host_fragment)
                .navigate(R.id.action_list_to_detail, args);
    }
}
