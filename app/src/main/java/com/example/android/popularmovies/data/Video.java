package com.example.android.popularmovies.data;

import java.util.List;

public class Video {
    private List<Result> results;

    public Video(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }
}
