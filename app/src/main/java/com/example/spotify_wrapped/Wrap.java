package com.example.spotify_wrapped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import android.util.Log;

public class Wrap {
    private HashMap<String, Track> topTracks =  new HashMap<>();
    private HashMap<String, Artist> topArtists = new HashMap<>();
    private HashMap<String, String> topGenres = new HashMap<>();
    private String dateMade;
    private String dateFrom;

    public Wrap(Long dateMade, Long dateFrom) {
        this.dateMade = String.valueOf(dateMade);
        this.dateFrom = String.valueOf(dateFrom);
    }

    public Wrap(HashMap<String, Object> wrap) {
        try {
            dateMade = (String) wrap.get("dateMade");
            dateFrom = (String) wrap.get("dateFrom");
            ArrayList<HashMap<String, Object>> tracks = (ArrayList<HashMap<String, Object>>) wrap.get("topTracks");
            for (int i = 1; i < tracks.size(); i++) {
                HashMap<String, Object> track = (HashMap<String, Object>) tracks.get(i);
                String albumImage = (String) track.get("albumImage");
                String albumName = (String) track.get("albumName");
                String id = (String) track.get("id");
                String trackName = (String) track.get("trackName");
                Track newTrack = new Track(trackName, albumName, albumImage, id);
                ArrayList<String> artists = (ArrayList<String>) track.get("artists");
                for (int j = 0; j < artists.size(); j++) {
                    newTrack.setArtists(String.valueOf(j), artists.get(j));
                }
                setTrack(String.valueOf(i), newTrack);
            }
            ArrayList<HashMap<String,Object>> artists = (ArrayList<HashMap<String, Object>>) wrap.get("topArtists");
            for (int i = 1; i < artists.size(); i++) {
                HashMap<String, Object> artist = (HashMap<String, Object>) artists.get(i);
                String artistImage = (String) artist.get("image");
                String artistName = (String) artist.get("name");
                String artistId = (String) artist.get("id");
                Artist newArtist = new Artist(artistName, artistImage, artistId);
                setArtist(String.valueOf(i), newArtist);
            }
            ArrayList<String> genres = (ArrayList<String>) wrap.get("topGenres");
            for (int i = 1; i < genres.size(); i++) {
                setGenre(String.valueOf(i), genres.get(i));
            }
        } catch (Exception e) {
            Log.wtf("Wrap constructor", "hashmap entered was invalid");
        }
    }
    public void setArtist(String key, Artist artist) {
        topArtists.put(key, artist);
    }
    public Artist getArtist(String key) {
        return topArtists.get(key);
    }
    public void setTrack(String key, Track track) {
        topTracks.put(key, track);
    }
    public HashMap<String, Track> getTopTracks(){return this.topTracks;}
    public HashMap<String, Artist> getTopArtists() { return this.topArtists;}
    public void setGenre(String key, String genre) {
        topGenres.put(key, genre);
    }
    public HashMap<String, String> getTopGenres() { return this.topGenres;}

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateMade() {
        return dateMade;
    }
}
