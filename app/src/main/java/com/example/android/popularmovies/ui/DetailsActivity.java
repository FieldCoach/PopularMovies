package com.example.android.popularmovies.ui;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.ApiKeyFile;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Result;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.example.android.popularmovies.utilities.MovieDbService;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// import com.example.android.popularmovies.InsertFavoritesLoader;

//import static com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class DetailsActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks{

    private static final String TAG = "PopM";

    private static final String MOVIE = "movie";
    private static final String DETAILS_REQUEST_URL = "details_request_url";
    private static final int DETAILS_LOADER = 2;
    private static final int INSERT_FAVS_LOADER = 4;
    private final static String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String REVIEWS = "reviews";
    private static final String TRAILERS = ",videos";

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private ShareActionProvider mShareActionProvider;
    private Intent shareIntent;
    private Uri shareUri;

    private Boolean favorite = false;
    private RecyclerView rvReviews;
    private RecyclerView rvMovieTrailers;
    private FloatingActionButton floatingActionButton;
    private CardView cvTrailers;
    private CardView cvReviews;

    /**
     * Gets the Intent from the MainActivity to retrieve Extras containing the details to display
     * in the UI
     * @param savedInstanceState contains data that was persisted
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intentFromMainActivity = getIntent();

        rvReviews = findViewById(R.id.rv_reviews);
        rvMovieTrailers = findViewById(R.id.rv_movie_trailers);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        cvTrailers = findViewById(R.id.cv_trailers);
        cvReviews = findViewById(R.id.cv_reviews);

        //Only attempt to get Extras from the intent if it has the Extras needed
        if (intentFromMainActivity.hasExtra(MOVIE)) {

            final Movie movie = intentFromMainActivity.getParcelableExtra(MOVIE);

            //Get the poster and backdrop
//            getPosterAndBackdrop(movie);

            //Get the text details
            getDetailsText(movie);

            //Load the trailers and reviews using the movieId
//            loadTrailersAndReviews(movie.getId());
            getMovieDetails(movie);

            //Setup the reviews RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            reviewAdapter = new ReviewAdapter(this);

            rvReviews.setLayoutManager(layoutManager);
            rvReviews.setAdapter(reviewAdapter);

            //Setup the movie trailer RecyclerView
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            trailerAdapter = new TrailerAdapter(this);
            SnapHelper snapHelper = new LinearSnapHelper();

            rvMovieTrailers.setLayoutManager(horizontalLayoutManager);
            rvMovieTrailers.setAdapter(trailerAdapter);
            snapHelper.attachToRecyclerView(rvMovieTrailers);

            //Setup the add to favorites button
            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Icon icon;
                    //Prevent favorites database changes offline
                    if (!NetworkUtils.isOnline(DetailsActivity.this)) {
                        Toast.makeText(DetailsActivity.this, "Favorites can't be modified offline", Toast.LENGTH_SHORT).show();
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

                    floatingActionButton.setImageIcon(icon);
                }
            });
        }
    }

    private void getMovieDetails(Movie movie) {

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
        String movieId = String.valueOf(movie.getId());
        Call<Movie> call = service.getDetails(movieId, ApiKeyFile.MOVIE_DB_API_KEY, REVIEWS + TRAILERS);

        // Use the method enqueue from the Call to act upon onResponse and onFailure
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();

                getDetailsText(movie);
                getPosterAndBackdrop(movie);
                loadTrailersAndReviews(movie);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("DetailsActivity", "onFailure: " + t.getMessage());
                // TODO: 6/28/2019 warn the user the movie didn't load
            }
        });
    }

    /**
     * Populates the TextViews with details about the movie
     * @param movie the movie object containing the details
     */
    private void getDetailsText(Movie movie) {
        //Set the text on the TextViews to show the Movie details
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvOverview = findViewById(R.id.tv_overview);
        TextView tvReleaseDate = findViewById(R.id.tv_release_date);

        tvTitle.setText(movie.getTitle());
        tvRating.setText(String.valueOf(movie.getVoteAverage()));
        tvOverview.setText(movie.getOverview());

        //Format the date before calling setText on the Text View
        //            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(movie.getReleaseDate());
//            String dateString = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).format(date);
        // TODO: 6/19/2019 Format release date properly
        String dateString = movie.getReleaseDate();
        tvReleaseDate.setText(dateString);
    }

    /**
     * Loads the poster and backdrop images from either the internet or the database depending on if
     * the movie is a favorite
     * @param movie the movie object whose images will be loaded
     */
    private void getPosterAndBackdrop(Movie movie) {
        ImageView ivDetailsPoster = findViewById(R.id.iv_details_poster);
        ImageView ivBackDrop = findViewById(R.id.iv_back_drop);

            //Load the poster into the ImageView
            Glide.with(this)
                    .load(movie.getPosterUriString())
                    .into(ivDetailsPoster);

            //Load the backdrop into its ImageView
            Glide.with(this)
                    .load(movie.getBackdropUriString())
                    .into(ivBackDrop);
    }

    /**
     * Removes a movie from the database by title
     * @param title the movie title
     */
    private void deleteFavoriteFromDb(String title) {
        /**
        String where = MovieEntry.COLUMN_TITLE + "=?";
        getContentResolver().delete(MovieEntry.CONTENT_URI, where, new String[]{title});
         */
    }

    /**
     * Adds a movie to the database
     * @param movie the movie to be added to the database
     */
    private void insertFavoriteToDb(Movie movie) {
        // TODO: 6/28/2019 Add movie to Room
    }

    //convert bitmap to byte array
    //https://stackoverflow.com/questions/11790104/how-to-storebitmap-image-and-retrieve-image-from-sqlite-database-in-android

    private void loadTrailersAndReviews(Movie movie) {
        //Remove the trailers and reviews from the layout if there is no network connection
        if (!NetworkUtils.isOnline(this)){
            Toast.makeText(this,"Trailers and reviews can't be viewed offline", Toast.LENGTH_SHORT).show();

            cvTrailers.setVisibility(View.GONE);
            cvReviews.setVisibility(View.GONE);
            return;
        }

        List<Result> reviewsList = movie.getReviews().getResults();
        if (reviewsList.size() > 0){
            reviewAdapter.setmReviewArrayList(reviewsList);
        } else {
            //Hide the reviews CardView if there are no reviews
            cvReviews.setVisibility(View.GONE);
        }

        //Save the trailers' data to an arrayList to send to its RecyclerView.Adapter
        List<Result> trailersList = movie.getVideos().getResults();
        if (trailersList.size() > 0){
            trailerAdapter.setTrailerArrayList(trailersList);
        } else {
            //Hide the trailers CardView if there are no trailers
            cvTrailers.setVisibility(View.GONE);
        }
    }

    @Override
    public Loader onCreateLoader(int id, final Bundle bundle) {
        switch (id){
            /**
            case DETAILS_LOADER:
                return new DetailsLoader(this, bundle, DETAILS_REQUEST_URL); */
            /**
            case INSERT_FAVS_LOADER:
                return new InsertFavoritesLoader(this, bundle, MOVIE); */
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()){
            case DETAILS_LOADER:
                try {
                    //Save the reviews' data to an arrayList to send to its RecyclerView.Adapter
                    Movie movie = null;
                    List<Result> reviewsList = movie.getReviews().getResults();
                    if (reviewsList.size() > 0){
                        reviewAdapter.setmReviewArrayList(reviewsList);
                    } else {
                        //Hide the reviews CardView if there are no reviews
                        cvReviews.setVisibility(View.GONE);
                    }

                    //Save the trailers' data to an arrayList to send to its RecyclerView.Adapter
                    List<Result> trailersList = movie.getVideos().getResults();
                    if (trailersList.size() > 0){
                        trailerAdapter.setTrailerArrayList(trailersList);
                    } else {
                        //Hide the trailers CardView if there are no trailers
                        cvTrailers.setVisibility(View.GONE);
                    }

                    //Get the first trailer's YouTube url
                    String BASE_URL = "https://www.youtube.com/watch";
//                    String trailerId = trailersList.get(0);

                    shareUri = Uri.parse(BASE_URL).buildUpon()
//                            .appendQueryParameter("v", trailerId)
                            .build();

                    //Add the url to an intent
                    shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareUri.toString());

                    setShareIntent(shareIntent);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
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
