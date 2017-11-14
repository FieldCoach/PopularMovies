package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private static final String DETAILS_REQUEST_URL = "details_request_url";
    private static final int DETAILS_LOADER = 2;

    private ActivityMovieDetailsBinding detailsBinding;

    private MovieTrailerAdapter movieTrailerAdapter;
    private ReviewAdapter reviewAdapter;

    private ShareActionProvider mShareActionProvider;
    private Intent shareIntent;
    private Uri shareUri;

    private Boolean favorite = false;

    /**
     * Gets the Intent from the MainActivity to retrieve Extras containing the details to display
     * in the UI
     * @param savedInstanceState contains data that was persisted
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        Intent intentFromMainActivity = getIntent();

        //Only attempt to get Extras from the intent if it has the Extras needed
        if (intentFromMainActivity.hasExtra(MOVIE)) {

            final Movie movie = intentFromMainActivity.getParcelableExtra(MOVIE);

            //Get the poster and backdrop
            getPosterAndBackdrop(movie);

            //Get the text details
            getDetailsText(movie);

            //Load the trailers and reviews using the movieId
            loadTrailersAndReviews(movie.getMovieId());

            //Setup the reviews RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            reviewAdapter = new ReviewAdapter(this);

            detailsBinding.inReviews.rvReviews.setLayoutManager(layoutManager);
            detailsBinding.inReviews.rvReviews.setAdapter(reviewAdapter);

            //Setup the movie trailer RecyclerView
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            movieTrailerAdapter = new MovieTrailerAdapter(this);
            SnapHelper snapHelper = new LinearSnapHelper();

            detailsBinding.inTrailers.rvMovieTrailers.setLayoutManager(horizontalLayoutManager);
            detailsBinding.inTrailers.rvMovieTrailers.setAdapter(movieTrailerAdapter);
            snapHelper.attachToRecyclerView(detailsBinding.inTrailers.rvMovieTrailers);

            //Setup the add to favorites button
            detailsBinding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Icon icon;
                    //Prevent favorites database changes offline
                    if (!NetworkUtils.isOnline(MovieDetailsActivity.this)) {
                        Toast.makeText(MovieDetailsActivity.this, "Favorites can't be modified offline", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Remove from favorites
                    if (favorite){
                        icon = Icon.createWithResource(view.getContext(), R.drawable.ic_favorite_black_48dp);
                        deleteFavoriteFromDb(movie.getTitle());
                        favorite = false;
                    //Add to favorites
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

    /**
     * Populates the TextViews with details about the movie
     * @param movie the movie object containing the details
     */
    private void getDetailsText(Movie movie) {
        //Set the text on the TextViews to show the Movie details
        detailsBinding.inTitle.tvTitle.setText(movie.getTitle());
        detailsBinding.inTitle.tvRating.setText(String.valueOf(movie.getVoteAverage()));
        detailsBinding.inOverview.tvOverview.setText(movie.getOverview());

        //Format the date before calling setText on the Text View
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(movie.getReleaseDate());
            String dateString = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).format(date);
            detailsBinding.inTitle.tvReleaseDate.setText(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the poster and backdrop images from either the internet or the database depending on if
     * the movie is a favorite
     * @param movie the movie object whose images will be loaded
     */
    private void getPosterAndBackdrop(Movie movie) {
        Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI,
                null,
                MovieEntry.COLUMN_TITLE + "=?",
                new String[]{movie.getTitle()},
                null);

        //If this movie is in the database, it is a favorite
        if (cursor.getCount() > 0){
            Icon icon = Icon.createWithResource(this, R.drawable.ic_favorite_red_48dp);
            detailsBinding.floatingActionButton.setImageIcon(icon);
            favorite = true;
        }

        //If this movie isn't a favorite, load the poster and backdrop from the internet
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
            //If this movie is a favorite, load the images from the byteArrays in the database
            cursor.moveToFirst();
            Bitmap poster = bitmapFromBytes(cursor.getBlob(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER)));
            Bitmap backdrop = bitmapFromBytes(cursor.getBlob(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP)));

            detailsBinding.ivDetailsPoster.setImageBitmap(poster);
            detailsBinding.ivBackDrop.setImageBitmap(backdrop);
        }
        cursor.close();
    }

    /**
     * Removes a movie from the database by title
     * @param title the movie title
     */
    private void deleteFavoriteFromDb(String title) {
        String where = MovieEntry.COLUMN_TITLE + "=?";
        getContentResolver().delete(MovieEntry.CONTENT_URI, where, new String[]{title});
    }

    /**
     * Adds a movie to the database
     * @param movie the movie to be added to the database
     */
    private void insertFavoriteToDb(Movie movie) {
        ContentValues values = new ContentValues();

        try {
            //Get the movie images from their URLs and store the data into byteArrays (to allow parceling)
            URL posterUrl = new URL(movie.getPosterLocationUriString());
            URL backdropUrl = new URL(movie.getBackdropLocationUriString());

            byte[] posterBytes = bytesFromImageUrl(posterUrl);
            byte[] backdropBytes = bytesFromImageUrl(backdropUrl);

            //Add the movie details to the database
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

    /**
     * Retrieves an byte array from the image's URL
     * @param imageUrl the image URL location
     * @return byte array
     * @throws IOException if the URL is invalid
     */
    private byte[] bytesFromImageUrl(URL imageUrl) throws IOException{
        InputStream inputStream = imageUrl.openStream();
        Bitmap image = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * Creates a bitmap from a byte array
     * @param bytes byte array containing image data
     * @return bitmap of the image
     */
    private Bitmap bitmapFromBytes(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadTrailersAndReviews(String movieId) {
        //Remove the trailers and reviews from the layout if there is no network connection
        if (!NetworkUtils.isOnline(this)){
            Toast.makeText(this,"Trailers and reviews offline can't be viewed offline", Toast.LENGTH_SHORT).show();
            detailsBinding.cvTrailers.setVisibility(View.GONE);
            detailsBinding.cvReviews.setVisibility(View.GONE);
            return;
        }

        //Build the url to request the trailers and reviews
        URL detailsRequestUrl = NetworkUtils.buildDetailsRequestUrl(movieId);

        //Store the url in a bundle
        Bundle urlBundle = new Bundle();
        urlBundle.putString(DETAILS_REQUEST_URL, detailsRequestUrl.toString());

        //Get the DETAILS_LOADER
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> detailsLoader = loaderManager.getLoader(DETAILS_LOADER);

        //Initialize/Restart the DETAILS_LOADER and pass it the bundle containing the url
        if (detailsLoader == null) {
            loaderManager.initLoader(DETAILS_LOADER, urlBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(DETAILS_LOADER, urlBundle, this).forceLoad();
        }
        Log.d(TAG, "loadTrailersAndReviews: was called");
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
                //Get the details request url
                String detailsRequestString = bundle.getString(DETAILS_REQUEST_URL);

                //If the url is null, there is nothing to load
                if (detailsRequestString == null || detailsRequestString.equals("")) return null;

                //Load the JSON response from the url
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
            //Save the reviews' data to an arrayList to send to its RecyclerView.Adapter
            ArrayList<Review> reviewArrayList = JSONDataHandler.getReviewArrayList(jsonResultString);
            reviewAdapter.setmReviewArrayList(reviewArrayList);

            //Save the trailers' data to an arrayList to send to its RecyclerView.Adapter
            ArrayList<String> trailerArrayList = JSONDataHandler.getTrailerArrayList(jsonResultString);
            movieTrailerAdapter.setTrailerArrayList(trailerArrayList);

            //Get the first trailer's YouTube url
            String BASE_URL = "https://www.youtube.com/watch";
            String trailerId = trailerArrayList.get(0);

            shareUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter("v", trailerId)
                    .build();

            //Add the url to an intent
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareUri.toString());

            setShareIntent(shareIntent);

        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //Not implemented
    }

    //Add a share action
    //https://developer.android.com/reference/android/support/v7/widget/ShareActionProvider.html

    /**
     * Adds a share action to the actionBar
     * @param menu the menu to be displayed
     * @return true to create the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return true;
    }

    /**
     * Sets the intent containing the data to be shared
     * @param shareIntent the intent containing the data
     */
    private void setShareIntent(Intent shareIntent){
        if (mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
