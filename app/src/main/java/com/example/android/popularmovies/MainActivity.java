package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITES = "favorites";
    private static final String PAGE = "mPage";
    private static final String SORT_BY = "sortBy";
    private static final String MOVIES_ARRAY_LIST = "moviesArrayList";
    private static final String CURRENT_SCROLL_POSITION = "currentScrollPosition";
    private static final String CONNECT_TO_CONTINUE = "Please connect to the Internet to continue";
    private static final String MOVIE_REQUEST_URL = "movie_request_url";

    private static ArrayList<Movie> moviesArrayList = new ArrayList<>();
    private static final String apiKey = ApiKeyFile.MOVIE_DB_API_KEY;
    private String sortBySelectionString = "popular";
    private String mPage = "1";
    private int currentScrollPosition;

    private ActivityMainBinding mainBinding;
    private MoviePosterAdapter moviePosterAdapter;
    private GridLayoutManager gridLayoutManager;

    public static final int MOVIES_LOADER = 1;

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Creates and format the RecyclerView and adds an EndlessRecyclerViewScrollListener to allow endless
     * scrolling.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configure the GridLayoutManager then set it as the layout manager of the RecyclerView
        int noOfColumns = calculateNoOfColumns(this);
        gridLayoutManager = new GridLayoutManager(this, noOfColumns);
        mainBinding.rvMoviePosters.setLayoutManager(gridLayoutManager);

        //Construct and set the adapter for the RecyclerView
        moviePosterAdapter = new MoviePosterAdapter(this);
        mainBinding.rvMoviePosters.setAdapter(moviePosterAdapter);

        //Construct a new Endless Scroll Listener and pass it the GridLayoutManager
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                //Use field page instead of the parameter for this method
                //Using the parameter page caused the same page of result to be loaded multiple times
                setmPage(Integer.valueOf(mPage) + 1);
                Log.d(TAG, "onLoadMore.page: " + String.valueOf(page + 1));
                Log.d(TAG, "onLoadMore.mPage: " + String.valueOf(mPage + 1));
                getMovies();
            }
        };
        //Add an OnScrollListener to the RecyclerView and pass it the Endless Scroll Listener
        mainBinding.rvMoviePosters.addOnScrollListener(scrollListener);

        //If savedInstanceState isn't null, restore the app's previous state
        if (savedInstanceState != null) {
            mPage = savedInstanceState.getString(PAGE);
            sortBySelectionString = savedInstanceState.getString(SORT_BY);
            currentScrollPosition = savedInstanceState.getInt(CURRENT_SCROLL_POSITION, 0);

            moviesArrayList = savedInstanceState.getParcelableArrayList(MOVIES_ARRAY_LIST);
            moviePosterAdapter.setMoviesArrayList(moviesArrayList);

            //Scroll to the previous position
            mainBinding.rvMoviePosters.smoothScrollToPosition(currentScrollPosition);
        } else {
            moviesArrayList.clear();
            getMovies();
        }
        getSupportLoaderManager().initLoader(MOVIES_LOADER, null, this);
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
        return (int) (dpWidth / scalingFactor);
    }

    /**
     * Saves the data needed to restore the RecyclerView's previous state
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SCROLL_POSITION, currentScrollPosition);
        outState.putParcelableArrayList(MOVIES_ARRAY_LIST , moviesArrayList);
        outState.putString(PAGE, mPage);
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
     * Checks whether the device is online or currently connecting
     * @return
     */
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

    /**
     * Gets the URL used to request movies then executes doInBackground()
     */
    private void getMovies() {
        URL movieRequestUrl = NetworkUtils.buildMovieDbUrl(sortBySelectionString, mPage);

        Bundle urlBundle = new Bundle();
        urlBundle.putString(MOVIE_REQUEST_URL, movieRequestUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieLoader = loaderManager.getLoader(MOVIES_LOADER);

        if (movieLoader == null){
            loaderManager.initLoader(MOVIES_LOADER, urlBundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER, urlBundle, this);
        }
        Log.d(TAG, "getMovies: was called");
    }

    /**
     * Sets which mPage of results to display
     * @param mPage
     */
    private void setmPage(int mPage) {
        this.mPage = String.valueOf(mPage);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle bundle) {
        Log.d(TAG, "onCreateLoader() returned: " + "was called");
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
                // TODO: 11/9/2017 onStartLoading() - handle loading progressbar
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String movieRequestUrlString = bundle.getString(MOVIE_REQUEST_URL);

                if (movieRequestUrlString == null || movieRequestUrlString.equals("")) return null;

                try {
                    URL movieRequestUrl = new URL(movieRequestUrlString);
                    String jsonResultString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                    Log.d(TAG, "loadInBackground() returned: " + jsonResultString);
                    return jsonResultString;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String jsonResultString) {
        try {
            if (moviesArrayList == null) {
                moviesArrayList = JSONDataHandler.getMovieArrayList(jsonResultString);
            } else {
                ArrayList<Movie> moreMovies = JSONDataHandler.getMovieArrayList(jsonResultString);
                moviesArrayList.addAll(moreMovies);
            }

            moviePosterAdapter.setMoviesArrayList(moviesArrayList);

        } catch (NullPointerException e) {     //catching JSONException and NullPointerException
            e.printStackTrace();
            if (!isOnline()) {
                notifyConnectionError();
                // TODO: 11/9/2017 onPostExecute() - get movies from the favorites database
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

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
     * from the ArrayList, notifies the Adapter, resets the mPage count, then gets new movie results
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
        if (id == R.id.favorites){
            Log.d(TAG, "onOptionsItemSelected: favorites was called");

            // TODO: 11/9/2017 onOptionsItemSelected() - load favorite movies
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Clears the ArrayList of Movies, notifies the adapter, then gets new Movies to store in the
     * ArrayList
     */
    private void getNewMovieResults() {
        int size = moviesArrayList.size();

        //removes old movie results before getting new results
        moviesArrayList.clear();
        moviePosterAdapter.notifyItemRangeRemoved(0, size);

        //resets mPage count for new movie results
        setmPage(1);
        getMovies();
    }
}
