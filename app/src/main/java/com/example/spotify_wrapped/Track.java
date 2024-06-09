package com.example.spotify_wrapped;

import java.util.LinkedHashMap;

public class Track {
    private String trackName;
    private String albumName;
    private String albumImage;
    private String id;

    private LinkedHashMap<String, String> artists = new LinkedHashMap<>(6);

    public Track (String trackName, String albumName, String albumImage, String spotifyId) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.albumImage = albumImage;
        this.id = spotifyId;
    }
    public String getTrackName() {
        return this.trackName;
    }
    public String getAlbumName() {
        return this.albumName;
    }
    public String getAlbumImage() {
        return this.albumImage;
    }
    public void setArtists(String key, String artist) {
        this.artists.put(key, artist);
    }
    public LinkedHashMap<String, String> getArtists() {
        return this.artists;
    }
    public String getId() {
        return this.id;
    }
}
