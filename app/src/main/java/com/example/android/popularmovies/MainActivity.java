package com.example.android.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.EndlessRecyclerViewScrollListener;
import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ENTER_API_KEY = "Enter API Key";
    private static final String POSITIVE_BUTTON_OK = "OK";
    private static final String CONNECT_TO_CONTINUE = "Please connect to the Internet to continue";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String ENTER_VALID_API = "Please enter a valid API Key";
    private static final String CURRENT_SCROLL_POSITION = "currentScrollPosition";
    private static final String PAGE = "page";
    private static final String SORT_BY = "sortBy";
    private static final String API_KEY = "apiKey";

    private MoviePosterAdapter moviePosterAdapter;
    private String sortBySelectionString = "popular";
    private String apiKey;

    private static ArrayList<JSONObject> movieObjectsArray;
    private RecyclerView rvMoviePosters;
    private EndlessRecyclerViewScrollListener scrollListener;
    private String page = "1";
    private GridLayoutManager gridLayoutManager;
    private int currentScrollPosition;

    /**
     * Creates and format the RecyclerView, adds an EndlessRecyclerViewScrollListener to allow endless
     * scrolling, and if an API Key hasn't been provided, requests one.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMoviePosters = (RecyclerView) findViewById(R.id.rv_movie_posters);

        int noOfColumns = calculateNoOfColumns(this);
        gridLayoutManager = new GridLayoutManager(this, noOfColumns);
        rvMoviePosters.setLayoutManager(gridLayoutManager);

        moviePosterAdapter = new MoviePosterAdapter(this);
        rvMoviePosters.setAdapter(moviePosterAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                setPage(page+1);
                Log.d(TAG, "onLoadMore.page: " + String.valueOf(page+1));
                getMovies();
            }
        };
        rvMoviePosters.addOnScrollListener(scrollListener);
        //uncomment when using API set
        //********For Testing********//
//        apiKey = -REMOVED-;
        //********For Testing********//


        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate.apiKey: " + apiKey);
            requestApiKey();
        } else {
            page = savedInstanceState.getString(PAGE);
            apiKey = savedInstanceState.getString(API_KEY);
            sortBySelectionString = savedInstanceState.getString(SORT_BY);
            currentScrollPosition = savedInstanceState.getInt(CURRENT_SCROLL_POSITION, 0);
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
        outState.putString(PAGE, page);
        outState.putString(API_KEY, apiKey);
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
        if (apiKey != null){
            if (!apiKey.equals("")) {
                Log.d(TAG, "onResume: was called");
                getMovies();
                Log.d(TAG, "onResume.currentScrollPosition: " + String.valueOf(currentScrollPosition));

                //crude solution but it's the only thing that would work every time. Seems like this line of code
                //needs to be executed slightly after the images have loaded. There is a common issue with
                //smoothScrollToPosition and scrollToPosition working inconsistently
                /** https://stackoverflow.com/questions/30845742/smoothscrolltoposition-doesnt-work-properly-with-recyclerview **/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rvMoviePosters.smoothScrollToPosition(currentScrollPosition);
                    }
                }, 5);
            }
        }
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
    }

    /**
     * Prompts the user for their API Key. The dialog won't dismiss unless they enter one
     */
    private void requestApiKey() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View apiDialogLayout = layoutInflater.inflate(R.layout.api_dialog_layout, null);
        final EditText etApiInput = (EditText) apiDialogLayout.findViewById(R.id.et_api_dialog);

        AlertDialog.Builder apiDialogView = new AlertDialog.Builder(this);
        apiDialogView.setMessage(ENTER_API_KEY)
                .setView(apiDialogLayout)
                .setPositiveButton(POSITIVE_BUTTON_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Get the API Key from the user
                        apiKey = etApiInput.getText().toString();
                        Log.d(TAG, "requestApi.onClick() returned: " + apiKey);

                        if (apiKey != null && !apiKey.equals("")) {
                            Log.d(TAG, "requestApi.onClick: was called");
                            getMovies();
                        }
                    }
                })
                //can't dismiss without entering an API key
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (apiKey == null || apiKey.equals("")){
                            notifyApiInputError();
                        }
                    }
                })

                .show();
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
                if (movieObjectsArray == null) {
                    movieObjectsArray = JSONDataHandler.getMovieJSONObjectsArray(movieRequestResults);
                } else {
                    ArrayList<JSONObject> moreMovies = JSONDataHandler.getMovieJSONObjectsArray(movieRequestResults);
                    movieObjectsArray.addAll(moreMovies);
                }

                ArrayList<Uri> posterLocationsArray = JSONDataHandler.getPosterLocationsArray(movieObjectsArray);
                moviePosterAdapter.setMoviePosterLocationsArray(posterLocationsArray);

            } catch (NullPointerException e) {     //catching JSONException and NullPointerException
                e.printStackTrace();
                if (isOnline()) {
                    Log.d(TAG, "onPostExecute: notifyApiInputError was called");
                    notifyApiInputError();
                }else {
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
     * Notifies the user if the API key input was invalid.
     */
    private void notifyApiInputError() {
        Toast.makeText(getApplicationContext(), ENTER_VALID_API, Toast.LENGTH_LONG).show();
        requestApiKey();
    }

    /**
     * Called by the RecyclerView.Adapter
     * @return ArrayList of movie JSONObjects
     */
    public static ArrayList<JSONObject> getMovieObjectsArray(){
        return movieObjectsArray;
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
        int size = movieObjectsArray.size();

        //removes old movie results before getting new results
        movieObjectsArray.clear();
        moviePosterAdapter.notifyItemRangeRemoved(0, size);

        //resets page count for new movie results
        setPage(1);
        getMovies();
    }
}
