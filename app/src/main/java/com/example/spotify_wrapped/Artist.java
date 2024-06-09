package com.example.spotify_wrapped;

import java.util.LinkedHashMap;

/** Class to hold information about artists including image, genre, and name.
 *
 */
public class Artist {
    private String name;
    private String image;
    private LinkedHashMap<String, String> genres = new LinkedHashMap<>(10);
    private String id;

    public Artist(String name, String image, String spotifyId) {
        this.name = name;
        this.image = image;
        this.id = spotifyId;

    }
    public Artist(String name, String spotifyId) {
        this.name = name;
        this.id = spotifyId;
    }


    public String getName() {
        return name;
    }
    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }
    public void setGenres(String key, String genre){
        this.genres.put(key, genre);
    }
    public LinkedHashMap<String,String> getGenres(){
        return this.genres;
    }
}
