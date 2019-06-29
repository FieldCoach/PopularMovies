package com.example.android.popularmovies.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks  {

    private final static String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITES = "favorites";
    private static final String PAGE = "mPage";
    private static final String SORT_BY = "sortBy";
    private static final String MOVIES_ARRAY_LIST = "moviesArrayList";
    private static final String CURRENT_SCROLL_POSITION = "currentScrollPosition";
    private static final String MOVIE_REQUEST_URL = "movie_request_url";

    private static List<Movie> moviesArrayList = new ArrayList<>();
    private String sortBySelectionString = "popular";
    private String mPage = "1";
    private int currentScrollPosition;

    private PosterAdapter posterAdapter;
    private GridLayoutManager gridLayoutManager;

    public static final int MOVIES_LOADER = 1;
    private static final int FAVORITES_LOADER = 3;

    private static final String TAG = "PopM";

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
                    setmPage(Integer.valueOf(mPage) + 1);
                    getMovies();
                }
            }
        };
        //Add an OnScrollListener to the RecyclerView and pass it the Endless Scroll Listener
        rvMoviePosters.addOnScrollListener(scrollListener);

        checkConnectionStatus();

        getMovies();

        //If savedInstanceState isn't null, restore the app's previous state
//        if (savedInstanceState != null) {
//            mPage = savedInstanceState.getString(PAGE);
//            sortBySelectionString = savedInstanceState.getString(SORT_BY);
//            currentScrollPosition = savedInstanceState.getInt(CURRENT_SCROLL_POSITION, 0);
//
//            moviesArrayList = savedInstanceState.getParcelableArrayList(MOVIES_ARRAY_LIST);
//            posterAdapter.setMoviesArrayList(moviesArrayList, sortBySelectionString);
//
//            //Scroll to the previous position
//            if (currentScrollPosition > 0) {
//                rvMoviePosters.smoothScrollToPosition(currentScrollPosition);
//            }
//        } else {
//            //Otherwise, the movies still need to be retrieved
//            moviesArrayList.clear();
//             getMovies();
//        }
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
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt(CURRENT_SCROLL_POSITION, currentScrollPosition);
//        outState.putParcelableArrayList(MOVIES_ARRAY_LIST , moviesArrayList);
//        outState.putString(PAGE, mPage);
//        outState.putString(SORT_BY, sortBySelectionString);
//
//        super.onSaveInstanceState(outState);
//    }

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
    private void setmPage(int mPage) {
        this.mPage = String.valueOf(mPage);
    }

    /**
     *
     * @param id identifies the loader
     * @param bundle contains the movieRequestUrl for MOVIE_LOADER
     * @return MOVIE_LOADER or FAVORITES_LOADER
     */
    @Override
    public Loader onCreateLoader(final int id, final Bundle bundle) {
        /**
        switch (id){
            case MOVIES_LOADER:
                //Create and return the MoviesLoader. Pass in the bundle containing the MOVIE_REQUEST_URL
                return new MoviesLoader(this, MOVIE_REQUEST_URL, bundle);

            case FAVORITES_LOADER:
                //Create and return a CursorLoader that will load favorites from the database
                return new CursorLoader(this,
                        MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        MovieEntry.COLUMN_TITLE);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
        */
        // TODO 4: Loader returns null
        return null;
    }

    /**
     * Sets the data on posterAdapter that is needed to view the movie posters
     * @param loader the loader that was loading
     * @param data string or cursor returned from loader
     */
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        /**
        switch (loader.getId()){
            case MOVIES_LOADER:
                try {
                    //Cast the return data to a String
                    String jsonResultString = (String) data;
                    if (moviesArrayList == null) {
                        //Get an ArrayList containing the Movies
                        moviesArrayList = JSONDataHandler.getMovieArrayList(jsonResultString);
                    } else {
                        //Get an ArrayList containing more Movies and add them to the existing Movies
                        ArrayList<Movie> moreMovies = JSONDataHandler.getMovieArrayList(jsonResultString);
                        moviesArrayList.addAll(moreMovies);

                    }
                    //Send the Movies to the RecyclerView.Adapter so their posters can be displayed
                    posterAdapter.setMoviesArrayList(moviesArrayList, sortBySelectionString);

                } catch (NullPointerException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            case FAVORITES_LOADER:
                //FAVORITES_LOADER code shouldn't run if we aren't viewing favorites
                //Without this check, favorites can be loaded into the popular and top rated lists
                if(!sortBySelectionString.equals(FAVORITES)) return;

                if (moviesArrayList != null)
                    moviesArrayList.clear();

                //Cast the data to a Cursor
                Cursor cursor = (Cursor) data;
                while (cursor.moveToNext()){
                    //Save the Movie properties to the Movie object
                    Movie movie = new Movie.Builder()
                            .movieId(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)))
                            .title(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE)))
                            .overview(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_PLOT)))
                            .releaseDate(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE)))
                            .voteAverage(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RATING)))
                            .posterLocationUriString(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER)))
                            .backdropLocationUriString(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP)))
                            .build();
                    moviesArrayList.add(movie);
                }

                //Send the Movies to the RecyclerView.Adapter
                posterAdapter.setMoviesArrayList(moviesArrayList, sortBySelectionString);
                break;

        }*/
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //Not used
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

        //Create FAVORITES_LOADER
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> favoritesLoader = loaderManager.getLoader(FAVORITES_LOADER);

        //Initialize/Restart FAVORITES_LOADER
        if (favoritesLoader == null){
            loaderManager.initLoader(FAVORITES_LOADER, null, this);
        }
        if (favoritesLoader != null && sortBySelectionString.equals(FAVORITES)) {
            loaderManager.restartLoader(FAVORITES_LOADER, null, this);
        }
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