package com.example.android.popularmovies.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.popularmovies.ApiKeyFile;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movies;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies.utilities.MovieDbService;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final static String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String FAVORITES = "favorites";

    private static List<Movie> moviesArrayList = new ArrayList<>();
    private String sortBySelectionString = "popular";
    private String mPage = "1";

    private PosterAdapter posterAdapter;
    private GridLayoutManager gridLayoutManager;

    /**
     * Creates and format the RecyclerView and adds an EndlessRecyclerViewScrollListener to allow endless
     * scrolling.
     * @param savedInstanceState contains data that was persisted
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        //Configure the GridLayoutManager then set it as the layout manager of the RecyclerView
        int noOfColumns = calculateNoOfColumns(this);
        gridLayoutManager = new GridLayoutManager(this, noOfColumns);
        RecyclerView rvMoviePosters = findViewById(R.id.rv_movie_posters);

        rvMoviePosters.setLayoutManager(gridLayoutManager);

        //Construct and set the adapter for the RecyclerView
        posterAdapter = new PosterAdapter(this);
        rvMoviePosters.setAdapter(posterAdapter);

        //Construct a new Endless Scroll Listener and pass it the GridLayoutManager
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Use field page instead of the parameter for this method
                //Using the parameter page caused the same page of result to be loaded multiple times
                if (!sortBySelectionString.equals(FAVORITES)){
                    setPageNumber(Integer.valueOf(mPage) + 1);
                    getMovies();
                }
            }
        };
        //Add an OnScrollListener to the RecyclerView and pass it the Endless Scroll Listener
        rvMoviePosters.addOnScrollListener(scrollListener);
        checkConnectionStatus();
        getMovies();
    }

    /**
     * Saves the current scroll position
     */
    @Override
    protected void onPause() {
        super.onPause();
        // TODO 6/29/2018: Not sure what is this scroll position is used for? - Emre
        int currentScrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
    }

    /**
     * Inflates the main menu
     * @param menu the menu to be inflated
     * @return true, representing this was done successfully
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Changes sortBySelection to the value of the selected menu item, checks if the device is online
     * (except with favorites), then loads movies based off of the selected item
     *
     * @param item menu item that was selected
     * @return true representing it was done successfully
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Calculates the number of columns for the GridLayoutManager
     * @param context the context
     * @return the number of columns
     */
    public static int calculateNoOfColumns (Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        return (int) (dpWidth / scalingFactor);
    }

    /**
     * Gets the URL used to request movies then starts the MOVIE_LOADER
     *
     * getMovies() method is deprecated.
     */
    private void getMovies() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Build Http Client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);

        // Build Retrofit Object with Base URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        // Create the Service Object containing the @GET call
        MovieDbService service = retrofit.create(MovieDbService.class);

        // Create the Call by calling the @GET method from the Service
        Call<Movies> call = service.getSortedMovies("popular", ApiKeyFile.MOVIE_DB_API_KEY, mPage);

        Log.d("MainActivity", "getMovies: " + call.toString());
        Log.d("MainActivity", "getMovies: " + mPage);

        // Use the method enqueue from the Call to act upon onResponse and onFailure
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Movies movies = response.body();

                if (moviesArrayList == null) {
                    // Get an ArrayList containing the Movies
                    moviesArrayList = movies.getMovies();
                } else {
                    // Get an ArrayList containing more Movies and add them to the existing Movies
                    List<Movie> moreMovies = movies.getMovies();
                    moviesArrayList.addAll(moreMovies);

                }
                posterAdapter.updateMoviesList(moviesArrayList);
                Log.d("MainActivity", "onResponse: " + mPage);
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Log.d("MainActivity", "onFailure: " + t.getMessage());
            }
        });
    }

    /**
     * Sets which mPage for results to display
     * @param mPage the page to display results from
     */
    private void setPageNumber(int mPage) {
        this.mPage = String.valueOf(mPage);
    }

    /**
     * Checks for a network connection. If there isn't one, prompts the user if they would like to
     * view favorites offline or change the network settings.
     * @return boolean representing connection status
     */
    private boolean checkConnectionStatus() {
        //Check the connection status
        boolean connected = NetworkUtils.isOnline(this);
        //If there is no network connection, alert the user and prompt for next action
        if (!connected) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                    .setTitle("Offline")
                    .setMessage("Favorites can still be viewed. Would you like to continue?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // getFavoriteMovies();
                        }
                    })
                    .setNegativeButton("Network Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    });
            final AlertDialog dialog = alert.create();
            dialog.show();
        }
        return connected;
    }
}
