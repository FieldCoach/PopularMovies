package com.example.android.popularmovies;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView rvMoviePosters;
    private Spinner spnrSortBy;

    private MoviePosterAdapter moviePosterAdapter;
    private String sortBySelectionString = "popular";
    private String apiKey;

    private static ArrayList<JSONObject> movieObjectsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMoviePosters = (RecyclerView) findViewById(R.id.rv_movie_posters);
        spnrSortBy = (Spinner) findViewById(R.id.spinner);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMoviePosters.setLayoutManager(gridLayoutManager);

        moviePosterAdapter = new MoviePosterAdapter();
        rvMoviePosters.setAdapter(moviePosterAdapter);
        //uncomment when using API set
        apiKey = "***REMOVED***";
        setUpSpinner();
        getMovies();
//        requestApiKey();
        // TODO: 8/3/2017 check for internet connection before continuing

    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.sort_by_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSortBy.setAdapter(arrayAdapter);

        spnrSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortBySelectionString = adapterView.getItemAtPosition(i).toString();
//                getMovies();      // TODO: 8/2/2017 figure out if getMovies() should be called here
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do nothing
            }
        });
    }

    private void getMovies() {
        URL movieRequestUrl = NetworkUtils.buildMovieDbUrl(sortBySelectionString, apiKey);
        new GetMoviesTask().execute(movieRequestUrl);
    }

    /**
     * Prompts the user for their API Key. The dialog won't dismiss unless they enter one
     * @return the API key
     */
    // TODO: 8/2/2017 returning null
    private void requestApiKey() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View apiDialogLayout = layoutInflater.inflate(R.layout.api_dialog_layout, null);
        final EditText etApiInput = (EditText) apiDialogLayout.findViewById(R.id.et_api_dialog);

        AlertDialog.Builder apiDialogView = new AlertDialog.Builder(this);
        apiDialogView.setMessage("Enter API Key")
                .setView(apiDialogLayout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Get the API Key from the user
                        apiKey = etApiInput.getText().toString();
                        Log.d(TAG, "requestApi.onClick() returned: " + apiKey);

                        if (apiKey != null && !apiKey.equals("")) {
                            setUpSpinner();
                            getMovies();
                        } else {
                            notifyApiInputError();        // TODO: 8/2/2017 handle API key error in onPostExecute
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Notify the user we cannot continue without the API Key
                        notifyApiInputError();        // TODO: 8/2/2017 handle API key error in onPostExecute
                    }
                })
                .show();
    }


    public class GetMoviesTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

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

        @Override
        protected void onPostExecute(String movieRequestResults) {
            super.onPostExecute(movieRequestResults);
            if (movieRequestResults == null){
                notifyApiInputError();
            } else {
                // TODO: 8/2/2017 populate ArrayList to pass to RecyclerView.Adapter
                try {
                    movieObjectsArray = JSONDataHandler.getMovieJSONObjectsArray(movieRequestResults);
                    ArrayList<Uri> posterLocationsArray = JSONDataHandler.getPosterLocationsArray(movieObjectsArray);

                    moviePosterAdapter.setMovieData(movieRequestResults, posterLocationsArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void notifyApiInputError() {
        Toast.makeText(getApplicationContext(), "Please enter a valid API Key", Toast.LENGTH_LONG).show();
        requestApiKey();
    }

    public static ArrayList<JSONObject> getMovieObjectsArray(){
        return movieObjectsArray;
    }
}
