package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;

import com.example.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private static final String MOVIE = "movie";

    private ActivityMovieDetailsBinding detailsBinding;
    private ArrayList<Review> reviewArrayList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;

    private ArrayList<String> trailerArrayList;
    private MovieTrailerAdapter movieTrailerAdapter;

    private Boolean favorite = false;

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

            final Movie movie = intentFromMainActivity.getParcelableExtra(MOVIE);

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

            LinearLayoutManager horizontalManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            movieTrailerAdapter = new MovieTrailerAdapter(this);
            SnapHelper snapHelper = new LinearSnapHelper();

            detailsBinding.inTrailers.rvMovieTrailers.setLayoutManager(horizontalManager);
            detailsBinding.inTrailers.rvMovieTrailers.setAdapter(movieTrailerAdapter);
            snapHelper.attachToRecyclerView(detailsBinding.inTrailers.rvMovieTrailers);

            detailsBinding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Icon icon;
                    if (favorite){
                        icon = Icon.createWithResource(view.getContext(), R.drawable.ic_favorite_black_48dp);
                        deleteFavoriteFromDb(movie.getTitle());
                        favorite = false;
                    } else {
                        icon = Icon.createWithResource(view.getContext(), R.drawable.ic_favorite_red_48dp);
                        insertFavoriteToDb(movie);
                        favorite = true;
                    }

                    detailsBinding.floatingActionButton.setImageIcon(icon);
                }
            });
        }
    }

    private void deleteFavoriteFromDb(String title) {
        String where = MovieEntry.COLUMN_TITLE + "=?";
        getContentResolver().delete(MovieEntry.CONTENT_URI, where, new String[]{title});
    }

    private void insertFavoriteToDb(Movie movie) {
        ContentValues values = new ContentValues();

        URL posterUrl;
        InputStream inputStream;

        try {
            posterUrl = new URL(movie.getPosterLocationUriString().toString());
            inputStream = posterUrl.openStream();
            Bitmap poster = BitmapFactory.decodeStream(inputStream);

            //convert bitmap to byte array
            //https://stackoverflow.com/questions/11790104/how-to-storebitmap-image-and-retrieve-image-from-sqlite-database-in-android
            byte[] posterBytes = bytesFromBitmap(poster);

            values.put(MovieEntry.COLUMN_POSTER, posterBytes);
            values.put(MovieEntry.COLUMN_PLOT, movie.getOverview());
            values.put(MovieEntry.COLUMN_RATING, movie.getVoteAverage());
            values.put(MovieEntry.COLUMN_RELEASE, movie.getReleaseDate());
            values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());

            getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] bytesFromBitmap(Bitmap poster) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        poster.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    private void getReviews(String movieId) {
            URL reviewsRequestUrl = NetworkUtils.buildReviewsDbUrl(movieId);
            new GetReviewsTask().execute(reviewsRequestUrl);
            Log.d(TAG, "getReviews: was called");
    }

    // TODO: 11/7/2017 GetReviewsTask - rename and make static
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

                trailerArrayList = JSONDataHandler.getTrailerArrayList(reviewsResultsString);
                movieTrailerAdapter.setTrailerArrayList(trailerArrayList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
