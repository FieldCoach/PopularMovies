package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.JSONDataHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView detailsPoster;
    private TextView title;
    private TextView rating;
    private TextView overview;
    private TextView releaseDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        detailsPoster = (ImageView) findViewById(R.id.iv_details_poster);
        title = (TextView) findViewById(R.id.tv_title);
        rating = (TextView) findViewById(R.id.tv_rating);
        overview = (TextView) findViewById(R.id.tv_overview);
        releaseDate = (TextView) findViewById(R.id.tv_release_date);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        String moviePoster = intent.getStringExtra("moviePoster");

        ArrayList<JSONObject> movieObjectsArray = MainActivity.getMovieObjectsArray();
        JSONObject movieObject = movieObjectsArray.get(position);


        Picasso.with(this)
                .load(moviePoster)
                .fit()
                .into(detailsPoster);

        title.setText(JSONDataHandler.getDetails(movieObject, "title"));
        try {
            rating.setText(String.valueOf(movieObject.getDouble("vote_average")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        overview.setText(JSONDataHandler.getDetails(movieObject, "overview"));
        releaseDate.setText(JSONDataHandler.getDetails(movieObject, "release_date"));

    }
}
