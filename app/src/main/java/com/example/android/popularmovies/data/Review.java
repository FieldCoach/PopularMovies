package com.example.android.popularmovies.data;

import java.util.List;

/**
 * Created by AaronC on 11/1/2017.
 */

public class Review {

    private List<Result> results;

    public Review(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }

}

