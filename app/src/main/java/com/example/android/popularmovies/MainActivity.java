package com.example.android.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.EndlessScrollListener;
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

    private ProgressBar progressBar;

    private MoviePosterAdapter moviePosterAdapter;
    private String sortBySelectionString = "popular";
    private String apiKey;

    private static ArrayList<JSONObject> movieObjectsArray;
    private RecyclerView rvMoviePosters;
    private EndlessScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMoviePosters = (RecyclerView) findViewById(R.id.rv_movie_posters);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        int noOfColumns = calculateNoOfColumns(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, noOfColumns);
        rvMoviePosters.setLayoutManager(gridLayoutManager);

        moviePosterAdapter = new MoviePosterAdapter(this);
        rvMoviePosters.setAdapter(moviePosterAdapter);

        scrollListener = new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {

                return false;
            }
        };
        //uncomment when using API set
        //********For Testing********//
//        apiKey = -REMOVED-;
        //********For Testing********//

        requestApiKey();
    }

    public static int calculateNoOfColumns (Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getMovies();
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
     * Show loading indicator during doInBackground()
     */
    private void showLoadingIndicator(){
        rvMoviePosters.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Show movie posters after doInBackground() is done
     */
    private void showMoviePosters(){
        progressBar.setVisibility(View.INVISIBLE);
        rvMoviePosters.setVisibility(View.VISIBLE);
    }

    /**
     * Gets the URL to request movies then executes doInBackground()
     */
    private void getMovies() {
        URL movieRequestUrl = NetworkUtils.buildMovieDbUrl(sortBySelectionString, apiKey);
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
                            getMovies();
                        } else {
                            notifyApiInputError();
                        }
                    }
                })

                .show();
    }


    private class GetMoviesTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingIndicator();
        }

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
            //if nothing is returned, hide loadingIndicator. RecyclerView will remain empty
            showMoviePosters();

            try {
                movieObjectsArray = JSONDataHandler.getMovieJSONObjectsArray(movieRequestResults);
                ArrayList<Uri> posterLocationsArray = JSONDataHandler.getPosterLocationsArray(movieObjectsArray);

                moviePosterAdapter.setMoviePosterLocationsArray(posterLocationsArray);
            } catch (NullPointerException e) {     //catching JSONException and NullPointerException
                e.printStackTrace();
                if (isOnline()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.popular){
            sortBySelectionString = POPULAR;
            getMovies();
            return true;
        }

        if (id == R.id.top_rated){
            sortBySelectionString = TOP_RATED;
            getMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
