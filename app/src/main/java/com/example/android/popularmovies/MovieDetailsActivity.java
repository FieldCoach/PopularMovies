package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String POSITION = "position";
    private static final String MOVIE_ARRAY_LIST = "moviesArrayList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ImageView detailsPoster = (ImageView) findViewById(R.id.iv_details_poster);
        TextView title = (TextView) findViewById(R.id.tv_title);
        TextView rating = (TextView) findViewById(R.id.tv_rating);
        TextView overview = (TextView) findViewById(R.id.tv_overview);
        TextView releaseDate = (TextView) findViewById(R.id.tv_release_date);

        Intent intentFromMainActivity = getIntent();

        if (intentFromMainActivity.hasExtra(POSITION) &&
                intentFromMainActivity.hasExtra(MOVIE_ARRAY_LIST)) {

            int position = intentFromMainActivity.getIntExtra(POSITION, 0);
            ArrayList<Movie> movieArrayList = intentFromMainActivity.getParcelableArrayListExtra(MOVIE_ARRAY_LIST);

            Movie movie = movieArrayList.get(position);

            Picasso.with(this)
                    .load(movie.getPosterLocationUriString())
                    .fit()
                    .into(detailsPoster);

            title.setText(movie.getTitle());
            rating.setText(String.valueOf(movie.getVoteAverage()));
            overview.setText(movie.getOverview());
            releaseDate.setText(movie.getReleaseDate());
        }
    }
}
