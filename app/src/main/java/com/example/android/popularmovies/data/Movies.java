package com.example.android.popularmovies.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO 7/1/2019 CODE CLEAN-UP:
 *  We need to find a way to merge this class with Movie class.
 *  Or some other way to map the JSON object with our model - Emre
 */
public class Movies {

    @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();

    public List<Movie> getMovies() {
        return movies;
    }

    public class Review {

        private List<Result> results;

        public Review(List<Result> results) {
            this.results = results;
        }

        public List<Result> getResults() {
            return results;
        }

    }

    public class Video {
        private List<Result> results;

        public Video(List<Result> results) {
            this.results = results;
        }

        public List<Result> getResults() {
            return results;
        }
    }

    public class Result {
        // For Reviews Only
        private String author;
        private String content;

        // For Videos Only
        private String key;
        private String name;
        private String site;
        private String type;

        // Constructor for Video use
        public Result(String key, String name, String site, String type) {
            this.key = key;
            this.name = name;
            this.site = site;
            this.type = type;
        }

        // Constructor for Review use
        public Result(String author, String content) {
            this.author = author;
            this.content = content;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public String getSite() {
            return site;
        }

        public String getType() {
            return type;
        }

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }
    }


}


