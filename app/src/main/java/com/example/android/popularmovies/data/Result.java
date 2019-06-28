package com.example.android.popularmovies.data;

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
