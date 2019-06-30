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


import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.ApiKeyFile;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.ui.ReviewAdapter;
import com.example.android.popularmovies.ui.TrailerAdapter;
import com.example.android.popularmovies.utilities.MovieDbService;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {

    private static final String ARG_MOVIE_ID = "movie_id";
    private final static String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String REVIEWS = "reviews";
    private static final String TRAILERS = ",videos";

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private ShareActionProvider mShareActionProvider;
    private Boolean favorite = false;
    private FloatingActionButton floatingActionButton;
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

    public static MovieDetailsFragment newInstance(int movieId) {
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        RecyclerView rvReviews = view.findViewById(R.id.rv_reviews);
        RecyclerView rvMovieTrailers = view.findViewById(R.id.rv_movie_trailers);
        cvTrailers = view.findViewById(R.id.cv_trailers);
        cvReviews = view.findViewById(R.id.cv_reviews);
        tvTitle = view.findViewById(R.id.tv_title);
        tvRating = view.findViewById(R.id.tv_rating);
        tvOverview = view.findViewById(R.id.tv_overview);
        tvReleaseDate = view.findViewById(R.id.tv_release_date);
        ivDetailsPoster = view.findViewById(R.id.iv_details_poster);
        ivBackDrop = view.findViewById(R.id.iv_back_drop);
        String movieIdString = String.valueOf(movieId);
        getMovieDetails(movieIdString);
        //Setup the reviews RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        reviewAdapter = new ReviewAdapter(getContext());
        rvReviews.setLayoutManager(layoutManager);
        rvReviews.setAdapter(reviewAdapter);
        //Setup the movie trailer RecyclerView
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(),
                                                            LinearLayoutManager.HORIZONTAL,
                                                            false);
        trailerAdapter = new TrailerAdapter(getActivity());
        SnapHelper snapHelper = new LinearSnapHelper();
        rvMovieTrailers.setLayoutManager(horizontalLayoutManager);
        rvMovieTrailers.setAdapter(trailerAdapter);
        snapHelper.attachToRecyclerView(rvMovieTrailers);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        //Setup the add to favorites button
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Icon icon;
                //Prevent favorites database changes offline
                if (!NetworkUtils.isOnline(Objects.requireNonNull(getContext()))) {
                    Toast.makeText(getContext(),
                            "Favorites can't be modified offline",
                            Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                //Remove from favorites
                if (favorite){
                    icon = Icon.createWithResource(view.getContext(), R.drawable.ic_favorite_black_48dp);
                    //  deleteFavoriteFromDb(movie.getTitle());
                    favorite = false;
                    //Add to favorites
                } else {
                    icon = Icon.createWithResource(view.getContext(), R.drawable.ic_favorite_red_48dp);
                    //  insertFavoriteToDb(movie);
                    favorite = true;
                }
                floatingActionButton.setImageIcon(icon);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    }

    private void getMovieDetails(String movieIdString) {
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
        Call<Movie> call = service.getDetails(movieIdString, ApiKeyFile.MOVIE_DB_API_KEY, REVIEWS + TRAILERS);
        // Use the method enqueue from the Call to act upon onResponse and onFailure
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                getDetailsText(movie);
                getPosterAndBackdrop(movie);
                loadTrailersAndReviews(movie);
            }
            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("DetailsActivity", "onFailure: " + t.getMessage());
                // TODO: 6/28/2019 warn the user the movie didn't load - Aaron
            }
        });
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
        // TODO: 6/19/2019 Format release date properly - Aaron
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
        // TODO: 6/28/2019 Add movie to Room - Aaron
    }

    /**
     * Sets the intent containing the data to be shared
     * @param shareIntent the intent containing the data
     */
    private void setShareIntent(Intent shareIntent){
        if (mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}