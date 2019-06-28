package com.example.android.popularmovies.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Contains all of the details of a Movie that will be displayed in the UI
 */
@Entity
public class Movie implements Parcelable {
    @PrimaryKey
    private int id;
    @SerializedName("poster_path")
    private String posterPath;

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
    private Video videos;

    public Movie() {
    }

    public Movie(int id, String posterPath, String backdropPath, String title, String voteAverage, String overview, String releaseDate, Review reviews, Video videos) {
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

    private Movie(Builder builder) {
        id = builder.id;
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
        dest.writeInt(this.id);
        dest.writeString(this.posterPath);
        dest.writeString(this.backdropPath);
        dest.writeString(this.title);
        dest.writeString(this.voteAverage);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
    }


    private Movie(Parcel in) {
        this.id = in.readInt();
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

    public Review getReviews() {
        return reviews;
    }

    public Video getVideos() {
        return videos;
    }

    public void setVideos(Video videos) {
        this.videos = videos;
    }


    public static final class Builder {
        private int id;
        private String posterLocationUriString;
        private String backdropLocationUriString;
        private String title;
        private String voteAverage;
        private String overview;
        private String releaseDate;

        public Builder() {
        }

        public Builder movieId(int val) {
            id = val;
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
