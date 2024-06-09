package com.example.spotify_wrapped;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.spotify_wrapped.UserItemTimeFrame.LONG;
import static com.example.spotify_wrapped.UserItemTimeFrame.MEDIUM;
import static com.example.spotify_wrapped.UserItemTimeFrame.SHORT;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WrapFragment extends Fragment {

    private UserViewModel model;

    private String timeChoice;

    private Wrap currentWrap;

    private boolean thisIsAPastWrap;

    public WrapFragment() {
        thisIsAPastWrap = false;
    }

    public WrapFragment(Wrap clickedWrap) {
        this.currentWrap = clickedWrap;
        thisIsAPastWrap = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            timeChoice = bundle.getString("timeChoice");
        }
        return inflater.inflate(R.layout.fragment_wrapped, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        View wrapBackButton = view.findViewById(R.id.wrap_back_button);

        // CardView objects for next and back buttons of Wrapped items

        CardView topArtistsNext = view.findViewById(R.id.top_artists_next_button);
        CardView topSongsBack = view.findViewById(R.id.top_songs_back_button);
        CardView topSongsNext = view.findViewById(R.id.top_songs_next_button);
        CardView topGenresBack = view.findViewById(R.id.top_genres_back_button);
        CardView topGenresNext = view.findViewById(R.id.top_genres_next_button);
        CardView summaryBack = view.findViewById(R.id.summary_back_button);

        View topArtistsLayout = view.findViewById(R.id.top_artists_layout);
        View topSongsLayout = view.findViewById(R.id.top_songs_layout);
        View topGenresLayout = view.findViewById(R.id.top_genres_layout);
        View summaryLayout = view.findViewById(R.id.summary_layout);

        // Click listeners for top artists buttons

        topArtistsNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topArtistsLayout.setVisibility(View.GONE);
                topSongsLayout.setVisibility(View.VISIBLE);
            }
        });

        // Click listeners for top songs buttons

        topSongsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topArtistsLayout.setVisibility(View.VISIBLE);
                topSongsLayout.setVisibility(View.GONE);
            }
        });

        topSongsNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topGenresLayout.setVisibility(View.VISIBLE);
                topSongsLayout.setVisibility(View.GONE);            }
        });

        // Click listeners for top genres buttons

        topGenresBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topGenresLayout.setVisibility(View.GONE);
                topSongsLayout.setVisibility(View.VISIBLE);            }
        });

        topGenresNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topGenresLayout.setVisibility(View.GONE);
                summaryLayout.setVisibility(View.VISIBLE);            }
        });

        // Click listeners for summary buttons

        summaryBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                summaryLayout.setVisibility(View.GONE);
                topGenresLayout.setVisibility(View.VISIBLE);            }
        });

        if (!thisIsAPastWrap) {
            if (timeChoice.equals("1")) {
                currentWrap = model.makeNewWrapped(SHORT, getActivity());
            } else if (timeChoice.equals("2")) {
                currentWrap = model.makeNewWrapped(MEDIUM, getActivity());
            } else {
                currentWrap = model.makeNewWrapped(LONG, getActivity());
            }

            wrapBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replaceFragment(new ProfileFragment());
                }
            });
        } else {
            wrapBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replaceFragment(new ProfileFragment());
                }
            });
        }

        List<Artist> topArtistsList = null;
        List<Track> topTracksList = null;
        List<String> topGenresList = null;

        if (currentWrap.getTopArtists() != null) {
            HashMap<String, Artist> currentUserTopArtists = currentWrap.getTopArtists();
            topArtistsList = new ArrayList<>(currentUserTopArtists.values());
        }

        if (currentWrap.getTopTracks() != null) {
            HashMap<String, Track> currentUserTopTracks = currentWrap.getTopTracks();
            topTracksList = new ArrayList<>(currentUserTopTracks.values());
        }


        if (currentWrap.getTopGenres() != null) {
            HashMap<String, String> currentUserTopGenres = currentWrap.getTopGenres();
            topGenresList = new ArrayList<>(currentUserTopGenres.values());
        }

        // Set top artists information

        for (int i = 0; i < Math.min(topArtistsList.size(), 5); i++) {
            Artist artist = topArtistsList.get(i);
            TextView artistTextView = (TextView) getView().findViewById(getResources().getIdentifier("top_artist_name_" + (i + 1), "id", getActivity().getPackageName()));
            artistTextView.setText(artist.getName());
            artistTextView.setSelected(true);

            ImageView artistImageView = (ImageView) getView().findViewById(getResources().getIdentifier("top_artist_image_" + (i + 1), "id", getActivity().getPackageName()));
            new DownloadImageFromInternet(artistImageView, getActivity()).execute(topArtistsList.get(i).getImage());
        }

        // Set top songs information

        for (int i = 0; i < Math.min(topTracksList.size(), 5); i++) {
            Track track = topTracksList.get(i);
            TextView trackTextView = (TextView) getView().findViewById(getResources().getIdentifier("top_song_name_" + (i + 1), "id", getActivity().getPackageName()));
            trackTextView.setText(track.getTrackName());
            trackTextView.setSelected(true);

            ImageView songImageView = (ImageView) getView().findViewById(getResources().getIdentifier("top_song_image_" + (i + 1), "id", getActivity().getPackageName()));
            new DownloadImageFromInternet(songImageView, getActivity()).execute(topTracksList.get(i).getAlbumImage());
        }


        // Set top genres information

        for (int i = 0; i < Math.min(topGenresList.size(), 5); i++) {
            String genre = topGenresList.get(i);
            TextView genreTextView = (TextView) getView().findViewById(getResources().getIdentifier("top_genre_" + (i + 1), "id", getActivity().getPackageName()));
            genreTextView.setText(genre);
            genreTextView.setSelected(true);
        }


        // Set summary information

        for (int i = 0; i < Math.min(topArtistsList.size(), 5); i++) {
            Artist artist = topArtistsList.get(i);
            TextView artistTextView = (TextView) getView().findViewById(getResources().getIdentifier("artist_" + (i + 1), "id", getActivity().getPackageName()));
            artistTextView.setText((i + 1) + "    " + artist.getName());
            artistTextView.setSelected(true);
        }

        for (int i = 0; i < Math.min(topTracksList.size(), 5); i++) {
            Track track = topTracksList.get(i);
            TextView trackTextView = (TextView) getView().findViewById(getResources().getIdentifier("song_" + (i + 1), "id", getActivity().getPackageName()));
            trackTextView.setText((i + 1) + "    " + track.getTrackName());
            trackTextView.setSelected(true);
        }

        TextView summaryTopGenreTextView = (TextView) getView().findViewById(R.id.top_genre);
        summaryTopGenreTextView.setText(topGenresList.get(0));
        summaryTopGenreTextView.setSelected(true);

        new DownloadImageFromInternet((ImageView) getView().findViewById(R.id.top_track_album_image), getActivity()).execute(topTracksList.get(0).getAlbumImage());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public static Drawable loadImageFromUrl(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            return null;
        }
    }

    public static class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        Activity context;

        public DownloadImageFromInternet(ImageView imageView, Activity context) {
            this.imageView = imageView;
            this.context = context;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}