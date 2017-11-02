package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contains all of the details of a Movie that will be displayed in the UI
 */

public class Movie implements Parcelable {

    private final String id;
    private final String posterLocationUriString;
    private final String backdropLocationUriString;
    private final String title;
    private final String voteAverage;
    private final String overview;
    private final String releaseDate;

    private Movie(Builder builder) {
        id = builder.id;
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
        dest.writeString(this.id);
        dest.writeString(this.posterLocationUriString);
        dest.writeString(this.backdropLocationUriString);
        dest.writeString(this.title);
        dest.writeString(this.voteAverage);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
    }


    private Movie(Parcel in) {
        this.id = in.readString();
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
        private String id;
        private String posterLocationUriString;
        private String backdropLocationUriString;
        private String title;
        private String voteAverage;
        private String overview;
        private String releaseDate;

        public Builder() {
        }

        public Builder id(String val) {
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

    String getId() {
        return id;
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
