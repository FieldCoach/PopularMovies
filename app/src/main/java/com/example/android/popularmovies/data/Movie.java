package com.example.android.popularmovies.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Contains all of the details of a Movie that will be displayed in the UI
 */
@Entity
public class Movie implements Parcelable {
    @PrimaryKey
    private int id;

    private final String movieId;
    @SerializedName("poster_path")
    private final String posterPath;
    @SerializedName("backdrop_path")
    private final String backdropPath;
    private final String title;
    private final String voteAverage;
    private final String overview;
    private final String releaseDate;

    private final static String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private final static String IMAGE_SIZE = "w342";

    public Movie(String movieId, String posterPath, String backdropPath, String title, String voteAverage, String overview, String releaseDate) {
        this.movieId = movieId;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.title = title;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    private Movie(Builder builder) {
        movieId = builder.movieId;
        posterPath = builder.posterLocationUriString;
        backdropPath = builder.backdropLocationUriString;
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
        dest.writeString(this.posterPath);
        dest.writeString(this.backdropPath);
        dest.writeString(this.title);
        dest.writeString(this.voteAverage);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
    }


    private Movie(Parcel in) {
        this.movieId = in.readString();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getMovieId() {
        return movieId;
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
