package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String PAGE = "page";
    private static final String SORT_BY = "sortBy";
    private static final String MOVIES_ARRAY_LIST = "moviesArrayList";
    private static final String CURRENT_SCROLL_POSITION = "currentScrollPosition";
    private static final String CONNECT_TO_CONTINUE = "Please connect to the Internet to continue";

    private static ArrayList<Movie> moviesArrayList = new ArrayList<>();
    private static final String apiKey = ApiKeyFile.API_KEY;
    private String sortBySelectionString = "popular";
    private String page = "1";
    private int currentScrollPosition;

    private RecyclerView rvMoviePosters;
    private MoviePosterAdapter moviePosterAdapter;
    private GridLayoutManager gridLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Creates and format the RecyclerView and adds an EndlessRecyclerViewScrollListener to allow endless
     * scrolling.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvMoviePosters = (RecyclerView) findViewById(R.id.rv_movie_posters);

        //Configure the GridLayoutManager then set it as the layout manager of the RecyclerView
        int noOfColumns = calculateNoOfColumns(this);
        gridLayoutManager = new GridLayoutManager(this, noOfColumns);
        rvMoviePosters.setLayoutManager(gridLayoutManager);

        //Construct and set the adapter for the RecyclerView
        moviePosterAdapter = new MoviePosterAdapter(this);
        rvMoviePosters.setAdapter(moviePosterAdapter);

        //Construct a new Endless Scroll Listener and pass it the GridLayoutManager
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                setPage(page+1);
                Log.d(TAG, "onLoadMore.page: " + String.valueOf(page+1));
                getMovies();
            }
        };
        //Add an OnScrollListener to the RecyclerView and pass it the Endless Scroll Listener
        rvMoviePosters.addOnScrollListener(scrollListener);

        //If savedInstanceState isn't null, restore the app's previous state
        if (savedInstanceState != null) {
            page = savedInstanceState.getString(PAGE);
            sortBySelectionString = savedInstanceState.getString(SORT_BY);
            currentScrollPosition = savedInstanceState.getInt(CURRENT_SCROLL_POSITION, 0);

            //Clear the ArrayList to ensure no duplicates
//            moviesArrayList.clear();
            moviesArrayList = savedInstanceState.getParcelableArrayList(MOVIES_ARRAY_LIST);
            moviePosterAdapter.setMoviesArrayList(moviesArrayList);

            rvMoviePosters.smoothScrollToPosition(currentScrollPosition);
        } else {
            moviesArrayList.clear();
            getMovies();
        }
    }

    /**
     * Calculates the number of columns for the GridLayoutManager
     * @param context
     * @return the number of columns
     */
    public static int calculateNoOfColumns (Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }

    /**
     * Saves the data needed to restore the RecyclerView's previous state
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SCROLL_POSITION, currentScrollPosition);
        outState.putParcelableArrayList(MOVIES_ARRAY_LIST , moviesArrayList);
        outState.putString(PAGE, page);
        outState.putString(SORT_BY, sortBySelectionString);
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
     * Restores movie results to the RecyclerView and the previous scroll position
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume.currentScrollPosition: " + String.valueOf(currentScrollPosition));
//        rvMoviePosters.smoothScrollToPosition(currentScrollPosition);
    }

    /**
     * Checks whether the device is online or connecting
     * @return
     */
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    /**
     * Gets the URL to request movies then executes doInBackground()
     */
    private void getMovies() {
        URL movieRequestUrl = NetworkUtils.buildMovieDbUrl(sortBySelectionString, apiKey, page);
        new GetMoviesTask().execute(movieRequestUrl);
        Log.d(TAG, "getMovies: was called");
    }

    public void setPage(int page) {
        this.page = String.valueOf(page);
    }


    private class GetMoviesTask extends AsyncTask<URL, Void, String> {

        /**
         * Retrieves a String of the JSON data from a GET request of the API
         * @param params the URL to use for the GET request
         * @return String of the JSON data
         */
        @Override
        protected String doInBackground(URL... params) {
            URL movieRequestUrl = params[0];
            String movieResultsString = null;
            try {
                movieResultsString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return movieResultsString;
        }

        /**
         * Gets movie JSONObjects and posterLocations to set data in RecyclerView.Adapter.
         * Notifies the user if there was an error with the internet connection or the API Key input.
         * @param movieRequestResults String of JSON data from the GET request of the API
         */
        @Override
        protected void onPostExecute(String movieRequestResults) {
            super.onPostExecute(movieRequestResults);

            try {
                if (moviesArrayList == null) {
                    moviesArrayList = JSONDataHandler.getMovieArrayList(movieRequestResults);
                } else {
                    ArrayList<Movie> moreMovies = JSONDataHandler.getMovieArrayList(movieRequestResults);
                    moviesArrayList.addAll(moreMovies);
                }

                moviePosterAdapter.setMoviesArrayList(moviesArrayList);

            } catch (NullPointerException e) {     //catching JSONException and NullPointerException
                e.printStackTrace();
                if (!isOnline()) {
                    notifyConnectionError();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Notifies the user if the device isn't connected to the internet then opens the wifi settings.
     */
    private void notifyConnectionError() {
        Toast.makeText(getApplicationContext(), CONNECT_TO_CONTINUE, Toast.LENGTH_LONG).show();
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    /**
     * Inflates the main menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    /**
     * Changes the selectionString to the value of the selected menu item, clears the current movies
     * from the ArrayList, notifies the Adapter, resets the page count, then gets new movie results
     * with the updated selectionString.
     * @param item menu item that was selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.popular){
            sortBySelectionString = POPULAR;
            Log.d(TAG, "onOptionsItemSelected: popular was called");

            getNewMovieResults();
            return true;
        }

        if (id == R.id.top_rated){
            sortBySelectionString = TOP_RATED;
            Log.d(TAG, "onOptionsItemSelected: top rated was called");

            getNewMovieResults();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getNewMovieResults() {
        int size = moviesArrayList.size();

        //removes old movie results before getting new results
        moviesArrayList.clear();
        moviePosterAdapter.notifyItemRangeRemoved(0, size);

        //resets page count for new movie results
        setPage(1);
        getMovies();
    }
}
