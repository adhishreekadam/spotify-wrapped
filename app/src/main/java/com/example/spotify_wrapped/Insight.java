package com.example.spotify_wrapped;

public class Insight {
    private String title;
    private String description;

    public Insight(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}