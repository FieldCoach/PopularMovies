package com.example.android.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final static String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITES = "favorites";

    // ************** Persist Across Lifecycles *************** //
    // This list is used to append more movies after scrolling all movies on a page (20 items)
    private static List<Movie> moviesArrayList = new ArrayList<>();
    private String sortBySelectionString = "popular";
    private String mPage = "1";
    private int currentScrollPosition;
    // ************** Persist Across Lifecycles *************** //

    private PosterAdapter posterAdapter;
    private GridLayoutManager gridLayoutManager;

    ActivityMainBinding mainBinding;

    private static final String TAG = "PopM";

    /**
     * Creates and format the RecyclerView and adds an EndlessRecyclerViewScrollListener to allow endless
     * scrolling.
     * @param savedInstanceState contains data that was persisted
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        //Configure the GridLayoutManager then set it as the layout manager of the RecyclerView
        int noOfColumns = calculateNoOfColumns(this);
        gridLayoutManager = new GridLayoutManager(this, noOfColumns);
        mainBinding.rvMoviePosters.setLayoutManager(gridLayoutManager);

        //Construct and set the adapter for the RecyclerView
        posterAdapter = new PosterAdapter(this);
        mainBinding.rvMoviePosters.setAdapter(posterAdapter);

        //Construct a new Endless Scroll Listener and pass it the GridLayoutManager
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Use field page instead of the parameter for this method
                //Using the parameter page caused the same page of result to be loaded multiple times
                if (!sortBySelectionString.equals(FAVORITES)){
                    setmPage(Integer.valueOf(mPage) + 1);
                    getMovies();
                }
            }
        };
        // Add an RecyclerView.OnScrollListener to the RecyclerView and pass it the Endless Scroll Listener
        mainBinding.rvMoviePosters.addOnScrollListener(scrollListener);

        checkConnectionStatus();

        getMovies();
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
     * Saves the data needed to restore the RecyclerView's previous state
     * @param outState the bundle containing the data to be persisted
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    /**
     * Saves the current scroll position
     */
    @Override
    protected void onPause() {
        super.onPause();
        currentScrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
    }

    /**
     * Gets the URL used to request movies then starts the MOVIE_LOADER
     */
    private void getMovies() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOVIE_DB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        MovieDbService service = retrofit.create(MovieDbService.class);

        Call<Movies> call = service.getSortedMovies("popular", ApiKeyFile.MOVIE_DB_API_KEY, mPage);

        Log.d("MainActivity", "getMovies: " + mPage);
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Movies movies = response.body();

                if (moviesArrayList == null) {
                    //Get an ArrayList containing the Movies
                    moviesArrayList = movies.getMovies();
                } else {
                    //Get an ArrayList containing more Movies and add them to the existing Movies
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
    private void setmPage(int mPage) {
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
                            getFavoriteMovies();
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

    /**
     * Inflates the main menu
     * @param menu the menu to be inflated
     * @return true, representing this was done successfully
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

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
        int id = item.getItemId();

        if (id == R.id.popular){
            sortBySelectionString = POPULAR;
            if (checkConnectionStatus())
                getNewMovieResults();

            return true;
        }

        if (id == R.id.top_rated){
            sortBySelectionString = TOP_RATED;
            if (checkConnectionStatus())
                getNewMovieResults();

            return true;
        }
        if (id == R.id.favorites){
            sortBySelectionString = FAVORITES;

            getFavoriteMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets sortBySelectionString to FAVORITES, then starts the FAVORITES_LOADER
     */
    private void getFavoriteMovies() {

        // TODO: 3/13/19 Load favorite movies
    }

    /**
     * Clears the ArrayList of Movies, notifies the adapter, then gets new Movies to store in the
     * ArrayList
     */
    private void getNewMovieResults() {
        //removes old movie results before getting new results
        moviesArrayList.clear();
//        posterAdapter.notifyItemRangeRemoved(0, size);
        posterAdapter.notifyDataSetChanged();

        //resets mPage count for new movie results
        setmPage(1);
        getMovies();
    }
}
