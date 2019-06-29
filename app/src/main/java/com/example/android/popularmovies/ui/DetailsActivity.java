package com.example.android.popularmovies.ui;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.ApiKeyFile;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Movies.Result;
import com.example.android.popularmovies.utilities.MovieDbService;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {

    private static final String MOVIE_ID = "movie_id";
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

    /**
     * Gets the Intent from the MainActivity to retrieve Extras containing the details to display
     * in the UI
     * @param savedInstanceState contains data that was persisted
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Intent intentFromMainActivity = getIntent();
        RecyclerView rvReviews = findViewById(R.id.rv_reviews);
        RecyclerView rvMovieTrailers = findViewById(R.id.rv_movie_trailers);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        cvTrailers = findViewById(R.id.cv_trailers);
        cvReviews = findViewById(R.id.cv_reviews);
        //Only attempt to get Extras from the intent if it has the Extras needed
        if (intentFromMainActivity.hasExtra(MOVIE_ID)) {
            int movieId = intentFromMainActivity.getIntExtra(MOVIE_ID, -1);
            String movieIdString = String.valueOf(movieId);
            getMovieDetails(movieIdString);
            //Setup the reviews RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            reviewAdapter = new ReviewAdapter(this);
            rvReviews.setLayoutManager(layoutManager);
            rvReviews.setAdapter(reviewAdapter);
            //Setup the movie trailer RecyclerView
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            trailerAdapter = new TrailerAdapter(this);
            SnapHelper snapHelper = new LinearSnapHelper();
            rvMovieTrailers.setLayoutManager(horizontalLayoutManager);
            rvMovieTrailers.setAdapter(trailerAdapter);
            snapHelper.attachToRecyclerView(rvMovieTrailers);
            //Setup the add to favorites button
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Icon icon;
                    //Prevent favorites database changes offline
                    if (!NetworkUtils.isOnline(DetailsActivity.this)) {
                        Toast.makeText(DetailsActivity.this, "Favorites can't be modified offline", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Remove from favorites
                    if (favorite){
                        icon = Icon.createWithResource(view.getContext(), R.drawable.ic_favorite_black_48dp);
//                        deleteFavoriteFromDb(movie.getTitle());
                        favorite = false;
                    //Add to favorites
                    } else {
                        icon = Icon.createWithResource(view.getContext(), R.drawable.ic_favorite_red_48dp);
//                        insertFavoriteToDb(movie);
                        favorite = true;
                    }
                    floatingActionButton.setImageIcon(icon);
                }
            });
        }
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
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvOverview = findViewById(R.id.tv_overview);
        TextView tvReleaseDate = findViewById(R.id.tv_release_date);
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
        ImageView ivDetailsPoster = findViewById(R.id.iv_details_poster);
        ImageView ivBackDrop = findViewById(R.id.iv_back_drop);
            //Load the poster into the ImageView
            Glide.with(this)
                    .load(movie.getPosterUriString())
                    .into(ivDetailsPoster);
            //Load the backdrop into its ImageView
            Glide.with(this)
                    .load(movie.getBackdropUriString())
                    .into(ivBackDrop);
    }

    /**
     * Adds a movie to the database
     * @param movie the movie to be added to the database
     */
    private void insertFavoriteToDb(Movie movie) {
        // TODO: 6/28/2019 Add movie to Room - Aaron
    }

    private void loadTrailersAndReviews(Movie movie) {
        //Remove the trailers and reviews from the layout if there is no network connection
        if (!NetworkUtils.isOnline(this)){
            Toast.makeText(this,
                    "Trailers and reviews can't be viewed offline",
                    Toast.LENGTH_SHORT)
                    .show();
            cvTrailers.setVisibility(View.GONE);
            cvReviews.setVisibility(View.GONE);
            return;
        }
        List<Result> reviewsList = movie.getReviews().getResults();
        if (reviewsList.size() > 0){
            reviewAdapter.setmReviewArrayList(reviewsList);
        } else {
            //Hide the reviews CardView if there are no reviews
            cvReviews.setVisibility(View.GONE);
        }
        //Save the trailers' data to an arrayList to send to its RecyclerView.Adapter
        List<Result> trailersList = movie.getVideos().getResults();
        if (trailersList.size() > 0){
            trailerAdapter.setTrailerArrayList(trailersList);
        } else {
            //Hide the trailers CardView if there are no trailers
            cvTrailers.setVisibility(View.GONE);
        }
    }

    /**
     * Adds a share action to the actionBar
     * @param menu the menu to be displayed
     * @return true to create the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
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
