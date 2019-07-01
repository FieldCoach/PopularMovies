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

package com.example.android.popularmovies.ui.details;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.ui.ReviewAdapter;
import com.example.android.popularmovies.ui.TrailerAdapter;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.viewmodel.MovieDetailsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    public static final String ARG_MOVIE_ID = "movie_id";

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private CardView cvTrailers;
    private CardView cvReviews;
    private TextView tvTitle;
    private TextView tvRating;
    private TextView tvOverview;
    private TextView tvReleaseDate;
    private ImageView ivDetailsPoster;
    private ImageView ivBackDrop;
    private int movieId;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        cvTrailers = view.findViewById(R.id.cv_trailers);
        cvReviews = view.findViewById(R.id.cv_reviews);
        tvTitle = view.findViewById(R.id.tv_title);
        tvRating = view.findViewById(R.id.tv_rating);
        tvOverview = view.findViewById(R.id.tv_overview);
        tvReleaseDate = view.findViewById(R.id.tv_release_date);
        ivDetailsPoster = view.findViewById(R.id.iv_details_poster);
        ivBackDrop = view.findViewById(R.id.iv_back_drop);
        // Get movie details from server
        // getMovieDetails(String.valueOf(movieId));
        //Setup the reviews RecyclerView
        RecyclerView rvReviews = view.findViewById(R.id.rv_reviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new ReviewAdapter(getContext());
        rvReviews.setAdapter(reviewAdapter);
        //Setup the movie trailer RecyclerView
        RecyclerView rvMovieTrailers = view.findViewById(R.id.rv_movie_trailers);
        rvMovieTrailers.setLayoutManager(
                        new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));
        trailerAdapter = new TrailerAdapter(getActivity());
        rvMovieTrailers.setAdapter(trailerAdapter);
        // private Boolean favorite = false;
        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        //Setup the add to favorites button
        floatingActionButton.setOnClickListener(v -> {
            // TODO 6/30/2019 : Implement FAB click - Emre
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MovieDetailsViewModel viewModel = ViewModelProviders
                .of(this)
                .get(MovieDetailsViewModel.class);
        // Set movieId
        viewModel.setMovieId(movieId);
        // Observe data
        viewModel.getMovieDetails().observe(this, movie -> {
            getDetailsText(movie);
            getPosterAndBackdrop(movie);
            loadTrailersAndReviews(movie);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details, menu);
    }

    /**
     * Populates the TextViews with details about the movie
     * @param movie the movie object containing the details
     */
    private void getDetailsText(Movie movie) {
        //Set the text on the TextViews to show the Movie details
        tvTitle.setText(movie.getTitle());
        tvRating.setText(String.valueOf(movie.getVoteAverage()));
        tvOverview.setText(movie.getOverview());
        /* TODO 6/19/2019(Original) Format release date properly - Aaron
         * TODO 7/1/2019(Update) BUG-FIX:
            * Either there is problem with JSON mapping
              or the response doesn't have a release date field, but
              the field comes out to be null - Emre
        */
        String dateString = movie.getReleaseDate();
        tvReleaseDate.setText(dateString);
    }

    /**
     * Loads the poster and backdrop images from either the internet or the database depending on if
     * the movie is a favorite
     * @param movie the movie object whose images will be loaded
     */
    private void getPosterAndBackdrop(Movie movie) {
        //Load the poster into the ImageView
        Glide.with(this)
                .load(movie.getPosterUriString())
                .into(ivDetailsPoster);
        //Load the backdrop into its ImageView
        Glide.with(this)
                .load(movie.getBackdropUriString())
                .into(ivBackDrop);
    }

    private void loadTrailersAndReviews(Movie movie) {
        //Remove the trailers and reviews from the layout if there is no network connection
        if (!NetworkUtils.isOnline(Objects.requireNonNull(getContext()))){
            Toast.makeText(getContext(),
                    "Trailers and reviews can't be viewed offline",
                    Toast.LENGTH_SHORT)
                    .show();
            cvTrailers.setVisibility(View.GONE);
            cvReviews.setVisibility(View.GONE);
            return;
        }
        List<Movies.Result> reviewsList = movie.getReviews().getResults();
        if (reviewsList.size() > 0){
            reviewAdapter.updateReviewList(reviewsList);
        } else {
            //Hide the reviews CardView if there are no reviews
            cvReviews.setVisibility(View.GONE);
        }
        //Save the trailers' data to an arrayList to send to its RecyclerView.Adapter
        List<Movies.Result> trailersList = movie.getVideos().getResults();
        if (trailersList.size() > 0){
            trailerAdapter.setTrailerArrayList(trailersList);
        } else {
            //Hide the trailers CardView if there are no trailers
            cvTrailers.setVisibility(View.GONE);
        }
    }

    /**
     * Adds a movie to the database
     * @param movie the movie to be added to the database
     */
    private void insertFavoriteToDb(Movie movie) {
        // TODO 6/28/2019 NEW FEAT: Add movie to Room - Aaron
    }
}