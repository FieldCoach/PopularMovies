package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMoviePosters;

    // TODO: 7/26/2017 create adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMoviePosters = (RecyclerView) findViewById(R.id.rv_movie_posters);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMoviePosters.setLayoutManager(gridLayoutManager);

        // TODO: 7/26/2017 set adapter
    }
}
