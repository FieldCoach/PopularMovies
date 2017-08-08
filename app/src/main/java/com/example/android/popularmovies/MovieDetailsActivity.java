package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE_POSTER = "moviePoster";
    public static final String POSITION = "position";
    public static final String TITLE = "title";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";

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
                intentFromMainActivity.hasExtra(MOVIE_POSTER)) {

            int position = intentFromMainActivity.getIntExtra(POSITION, 0);
            String moviePoster = intentFromMainActivity.getStringExtra(MOVIE_POSTER);

            ArrayList<JSONObject> movieObjectsArray = MainActivity.getMovieObjectsArray();
            JSONObject movieObject = movieObjectsArray.get(position);

            Picasso.with(this)
                    .load(moviePoster)
                    .fit()
                    .into(detailsPoster);

            title.setText(JSONDataHandler.getDetailsString(movieObject, TITLE));
            try {
                rating.setText(String.valueOf(movieObject.getDouble(VOTE_AVERAGE)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            overview.setText(JSONDataHandler.getDetailsString(movieObject, OVERVIEW));
            releaseDate.setText(JSONDataHandler.getDetailsString(movieObject, RELEASE_DATE));
        }
    }
}
