package com.example.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String MOVIE = "movie";
    private ActivityMovieDetailsBinding detailsBinding;

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

            //Set the text on the TextView to show the Movie details
            detailsBinding.tvTitle.setText(movie.getTitle());
            detailsBinding.tvRating.setText(String.valueOf(movie.getVoteAverage()));
            detailsBinding.tvOverview.setText(movie.getOverview());

            //Format the date before calling setText on the Text View
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(movie.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dateString = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).format(date);

            detailsBinding.tvReleaseDate.setText(dateString);
        }
    }
}
