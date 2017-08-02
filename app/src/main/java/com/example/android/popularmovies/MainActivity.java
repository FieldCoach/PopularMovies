package com.example.android.popularmovies;

import android.content.DialogInterface;
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

import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView rvMoviePosters;
    private EditText etApiDialog;
    private Spinner spSortBy;

    private MoviePosterAdapter moviePosterAdapter;
    private String sortBySelection = "Popular";
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMoviePosters = (RecyclerView) findViewById(R.id.rv_movie_posters);
//        etApiDialog = (EditText) findViewById(R.id.et_api_dialog);
        spSortBy = (Spinner) findViewById(R.id.spinner);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMoviePosters.setLayoutManager(gridLayoutManager);

        moviePosterAdapter = new MoviePosterAdapter();
        rvMoviePosters.setAdapter(moviePosterAdapter);

        apiKey = requestApiKey();
        if (apiKey != null) {
            setUpSpinner();
            getMovies();
        }
    }

    private void setUpSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSortBy.setAdapter(adapter);

        spSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortBySelection = adapterView.getItemAtPosition(i).toString();
                getMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do nothing
            }
        });
    }

    private void getMovies() {
        URL movieRequestUrl = NetworkUtils.buildUrl(sortBySelection, apiKey);
        new GetMoviesTask().execute(movieRequestUrl);
    }

    /**
     * Prompts the user for their API Key. The dialog won't dismiss unless they enter one
     * @return the API key
     */
    // TODO: 8/2/2017 returning null
    private String requestApiKey() {
        final String[] apiKey = new String[1];

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogLayout = inflater.inflate(R.layout.api_dialog_layout, null);
        etApiDialog = (EditText) dialogLayout.findViewById(R.id.et_api_dialog);

        AlertDialog.Builder apiKeyDialog = new AlertDialog.Builder(this);
        apiKeyDialog.setMessage("Enter API Key")
                .setView(dialogLayout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Get the API Key from the user
                        apiKey[0] = etApiDialog.getText().toString();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Notify the user we cannot continue without the API Key
                        Toast.makeText(getApplicationContext(), "Cannot continue without API Key", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
        Log.d(TAG, "requestApiKey() returned: " + apiKey[0]);
        return apiKey[0];
    }


    public class GetMoviesTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL movieRequestUrl = params[0];
            String movieRequestResults = null;
            try {
                movieRequestResults = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: 8/1/2017 consider returning JSON object instead of String
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(movieRequestResults);
                Log.d(TAG, "doInBackground() returned: " + jsonObject.toString(5));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movieRequestResults;
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
        }
    }
}
