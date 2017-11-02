package com.example.android.popularmovies;

/**
 * Created by AaronC on 11/1/2017.
 */

public class Review {
    private String author;
    private String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }

    String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
