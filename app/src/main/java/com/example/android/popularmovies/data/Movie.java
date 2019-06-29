package com.example.android.popularmovies.data;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.android.popularmovies.data.Movies.Review;
import com.google.gson.annotations.SerializedName;

@Entity
public class Movie {
    @PrimaryKey
    private int id;
    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;
    private String title;
    @SerializedName("vote_average")
    private String voteAverage;
    private String overview;
    private String releaseDate;

    private final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private final static String IMAGE_SIZE = "w342";

    @Ignore
    private Review reviews;

    @Ignore
    private Movies.Video videos;

    public Movie() {
    }

    public Movie(int id, String posterPath, String backdropPath, String title, String voteAverage, String overview, String releaseDate, Review reviews, Movies.Video videos) {
        this.id = id;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.title = title;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.reviews = reviews;
        this.videos = videos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Review getReviews() {
        return reviews;
    }

    public Movies.Video getVideos() {
        return videos;
    }

    public void setVideos(Movies.Video videos) {
        this.videos = videos;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setReviews(Review reviews) {
        this.reviews = reviews;
    }

    public String getPosterUriString() {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE)
                // "%2 is added to the front of each path. This needs to be removed
                .appendPath(posterPath.substring(1))
                .build();

        return builtUri.toString();
    }

    public String getBackdropUriString() {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(IMAGE_SIZE)
                .appendPath(backdropPath.substring(1))
                .build();

        return builtUri.toString();
    }
}
