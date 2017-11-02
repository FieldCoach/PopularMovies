package com.example.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private static final String MOVIE = "movie";

    private ActivityMovieDetailsBinding detailsBinding;
    private ArrayList<Review> reviewArrayList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    /**
     * Gets the Intent from the MainActivity to retrieve Extras containing the details to display
     * in the UI
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        Intent intentFromMainActivity = getIntent();

        //Only attempt to get Extras from the intent if it has the Extras needed
        if (intentFromMainActivity.hasExtra(MOVIE)) {

            Movie movie = intentFromMainActivity.getParcelableExtra(MOVIE);

            //Load the poster into the ImageView
            Picasso.with(this)
                    .load(movie.getPosterLocationUriString())
                    .fit()
                    .into(detailsBinding.ivDetailsPoster);

            //Load the backdrop into its ImageView
            Picasso.with(this)
                    .load(movie.getBackdropLocationUriString())
                    .fit()
                    .into(detailsBinding.ivBackDrop);

            //Set the text on the TextView to show the Movie details
            detailsBinding.inTitle.tvTitle.setText(movie.getTitle());
            detailsBinding.inTitle.tvRating.setText(String.valueOf(movie.getVoteAverage()));
            detailsBinding.inOverview.tvOverview.setText(movie.getOverview());

            //Format the date before calling setText on the Text View
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(movie.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dateString = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).format(date);

            detailsBinding.inTitle.tvReleaseDate.setText(dateString);

            //Construct URL for reviews using Movie id
            getReviews(movie.getId());

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            reviewAdapter = new ReviewAdapter(this);

            detailsBinding.inReviews.rvReviews.setLayoutManager(layoutManager);
            detailsBinding.inReviews.rvReviews.setAdapter(reviewAdapter);
        }
    }

    private void getReviews(String movieId) {
            URL reviewsRequestUrl = NetworkUtils.buildReviewsDbUrl(movieId);
            new GetReviewsTask().execute(reviewsRequestUrl);
            Log.d(TAG, "getReviews: was called");
    }

    private class GetReviewsTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL reviewsRequestUrl = urls[0];
            String reviewsResultsString = null;

            try {
                reviewsResultsString = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return reviewsResultsString;
        }

        @Override
        protected void onPostExecute(String reviewsResultsString) {
            super.onPostExecute(reviewsResultsString);

            try {
                reviewArrayList = JSONDataHandler.getReviewArrayList(reviewsResultsString);
                reviewAdapter.setmReviewArrayList(reviewArrayList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
