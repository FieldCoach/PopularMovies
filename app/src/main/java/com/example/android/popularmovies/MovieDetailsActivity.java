package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class MovieDetailsActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<String>{

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private static final String MOVIE = "movie";
    private static final int DETAILS_LOADER = 2;
    private static final String DETAILS_REQUEST_URL = "details_request_url";

    private ActivityMovieDetailsBinding detailsBinding;
    private ReviewAdapter reviewAdapter;

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

            //Find out if this movie is already a favorite
            Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI,
                    null,
                    MovieEntry.COLUMN_TITLE + "=?",
                    new String[]{movie.getTitle()},
                    null);

            if (cursor.getCount() > 0){
                Icon icon = Icon.createWithResource(this, R.drawable.ic_favorite_red_48dp);
                detailsBinding.floatingActionButton.setImageIcon(icon);
                favorite = true;
            }

            if (!favorite) {
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
            } else {
                // DONE: 11/10/2017 onCreate() - Load byteArrays from Movie object
                cursor.moveToFirst();
                Bitmap poster = bitmapFromBytes(cursor.getBlob(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER)));
                Bitmap backdrop = bitmapFromBytes(cursor.getBlob(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP)));

                detailsBinding.ivDetailsPoster.setImageBitmap(poster);
                detailsBinding.ivBackDrop.setImageBitmap(backdrop);
            }
            cursor.close();


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

            //Construct URL for reviews using Movie movieId
            getReviews(movie.getMovieId());

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

        try {
            URL posterUrl = new URL(movie.getPosterLocationUriString());
            URL backdropUrl = new URL(movie.getBackdropLocationUriString());

            byte[] posterBytes = bytesFromImageUrl(posterUrl);
            byte[] backdropBytes = bytesFromImageUrl(backdropUrl);

            values.put(MovieEntry.COLUMN_POSTER, posterBytes);
            values.put(MovieEntry.COLUMN_BACKDROP, backdropBytes);
            values.put(MovieEntry.COLUMN_PLOT, movie.getOverview());
            values.put(MovieEntry.COLUMN_RATING, movie.getVoteAverage());
            values.put(MovieEntry.COLUMN_RELEASE, movie.getReleaseDate());
            values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());

            getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //convert bitmap to byte array
    //https://stackoverflow.com/questions/11790104/how-to-storebitmap-image-and-retrieve-image-from-sqlite-database-in-android
    private byte[] bytesFromImageUrl(URL imageUrl) throws IOException{
        InputStream inputStream = imageUrl.openStream();
        Bitmap image = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    private Bitmap bitmapFromBytes(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void getReviews(String movieId) {
        if (!NetworkUtils.isOnline(this)){
            Toast.makeText(this,"Sorry, can't load trailers and reviews offline", Toast.LENGTH_SHORT).show();
            detailsBinding.cvTrailers.setVisibility(View.GONE);
            detailsBinding.cvReviews.setVisibility(View.GONE);
            return;
        }

        URL reviewsRequestUrl = NetworkUtils.buildReviewsDbUrl(movieId);

        Bundle urlBundle = new Bundle();
        urlBundle.putString(DETAILS_REQUEST_URL, reviewsRequestUrl.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> detailsLoader = loaderManager.getLoader(DETAILS_LOADER);

        if (detailsLoader == null) {
            loaderManager.initLoader(DETAILS_LOADER, urlBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(DETAILS_LOADER, urlBundle, this).forceLoad();
        }
        Log.d(TAG, "getReviews: was called");
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String detailsRequestString = bundle.getString(DETAILS_REQUEST_URL);

                if (detailsRequestString == null || detailsRequestString.equals("")) return null;

                try {
                    URL detailsRequestUrl = new URL(detailsRequestString);
                    String jsonResultString = NetworkUtils.getResponseFromHttpUrl(detailsRequestUrl);

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
            ArrayList<Review> reviewArrayList = JSONDataHandler.getReviewArrayList(jsonResultString);
            reviewAdapter.setmReviewArrayList(reviewArrayList);

            ArrayList<String> trailerArrayList = JSONDataHandler.getTrailerArrayList(jsonResultString);
            movieTrailerAdapter.setTrailerArrayList(trailerArrayList);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            //No reviews
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
