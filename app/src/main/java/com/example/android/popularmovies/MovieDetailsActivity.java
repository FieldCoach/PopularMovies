package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String POSITION = "position";
    private static final String MOVIE = "movie";

    /**
     * Gets the Intent from the MainActivity to retrieve Extras containing the details to display
     * in the UI
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView detailsPoster = (ImageView) findViewById(R.id.iv_details_poster);
        TextView titleTextView = (TextView) findViewById(R.id.tv_title);
        TextView ratingTextView = (TextView) findViewById(R.id.tv_rating);
        TextView overviewTextView = (TextView) findViewById(R.id.tv_overview);
        TextView releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);

        Intent intentFromMainActivity = getIntent();

        //Only attempt to get Extras from the intent if it has the Extras needed
        if (intentFromMainActivity.hasExtra(MOVIE)) {

            Movie movie = intentFromMainActivity.getParcelableExtra(MOVIE);

            //Load the poster into the ImageView
            Picasso.with(this)
                    .load(movie.getPosterLocationUriString())
                    .fit()
                    .into(detailsPoster);

            //Set the text on the TextView to show the Movie details
            titleTextView.setText(movie.getTitle());
            ratingTextView.setText(String.valueOf(movie.getVoteAverage()));
            overviewTextView.setText(movie.getOverview());

            //Format the date before calling setText on the Text View
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(movie.getReleaseDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dateString = new SimpleDateFormat("MMMM d, yyyy").format(date);

            releaseDateTextView.setText(dateString);
        }
    }
}
