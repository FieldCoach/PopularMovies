package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Contains all of the details of a Movie that will be displayed in the UI
 */

public class Movie implements Parcelable {

    @SerializedName("id")
    private final String movieId;
    @SerializedName("poster_path")
    private final String posterLocationUriString;
    @SerializedName("backdrop_path")
    private final String backdropLocationUriString;
    @SerializedName("original_title")
    private final String title;
    @SerializedName("vote_average")
    private final String voteAverage;
    @SerializedName("overview")
    private final String overview;
    @SerializedName("release_date")
    private final String releaseDate;

    public Movie(String movieId, String posterLocationUriString, String backdropLocationUriString, String title, String voteAverage, String overview, String releaseDate) {
        this.movieId = movieId;
        this.posterLocationUriString = posterLocationUriString;
        this.backdropLocationUriString = backdropLocationUriString;
        this.title = title;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    private Movie(Builder builder) {
        movieId = builder.movieId;
        posterLocationUriString = builder.posterLocationUriString;
        backdropLocationUriString = builder.backdropLocationUriString;
        title = builder.title;
        voteAverage = builder.voteAverage;
        overview = builder.overview;
        releaseDate = builder.releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.movieId);
        dest.writeString(this.posterLocationUriString);
        dest.writeString(this.backdropLocationUriString);
        dest.writeString(this.title);
        dest.writeString(this.voteAverage);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
    }


    private Movie(Parcel in) {
        this.movieId = in.readString();
        this.posterLocationUriString = in.readString();
        this.backdropLocationUriString = in.readString();
        this.title = in.readString();
        this.voteAverage = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public static final class Builder {
        private String movieId;
        private String posterLocationUriString;
        private String backdropLocationUriString;
        private String title;
        private String voteAverage;
        private String overview;
        private String releaseDate;

        public Builder() {
        }

        public Builder movieId(String val) {
            movieId = val;
            return this;
        }

        public Builder posterLocationUriString(String val) {
            posterLocationUriString = val;
            return this;
        }

        public Builder backdropLocationUriString(String val) {
            backdropLocationUriString = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder voteAverage(String val) {
            voteAverage = val;
            return this;
        }

        public Builder overview(String val) {
            overview = val;
            return this;
        }

        public Builder releaseDate(String val) {
            releaseDate = val;
            return this;
        }

        public Movie build() {
            return new Movie(this);
        }
    }

    String getMovieId() {
        return movieId;
    }

    String getPosterLocationUriString() {
        return posterLocationUriString;
    }

    String getBackdropLocationUriString() {
        return backdropLocationUriString;
    }

    String getTitle() {
        return title;
    }

    String getVoteAverage() {
        return voteAverage;
    }

    String getOverview() {
        return overview;
    }

    String getReleaseDate() {
        return releaseDate;
    }
}
