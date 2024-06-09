package com.example.spotify_wrapped;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.Response;
import okhttp3.ResponseBody;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link InsightsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class InsightsFragment extends Fragment {

    UserViewModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insights_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        User currentUser = model.getCurrentUser();

        Wrap wrap = model.makeNewWrappedInsights(UserItemTimeFrame.SHORT, getActivity());
        List<Artist> topArtistsList = null;
        List<Track> topTracksList = null;
        List<String> topGenresList = null;

        if (wrap.getTopArtists() != null) {
            HashMap<String, Artist> currentUserTopArtists = wrap.getTopArtists();
            topArtistsList = new ArrayList<>(currentUserTopArtists.values());
        }

        if (wrap.getTopTracks() != null) {
            HashMap<String, Track> currentUserTopTracks = wrap.getTopTracks();
            topTracksList = new ArrayList<>(currentUserTopTracks.values());
        }

        if (wrap.getTopGenres() != null) {
            HashMap<String, String> currentUserTopGenres = wrap.getTopGenres();
            topGenresList = new ArrayList<>(currentUserTopGenres.values());
        }

        StringBuilder prompt = new StringBuilder();
        if (topArtistsList != null) {
            prompt.append("Here are the users top artists: ");
            for (Artist a : topArtistsList) {
                for (int i = 0; i < a.getName().length(); i++) {
                    if (a.getName().charAt(i) =='"') {
                        prompt.append("'");
                    } else {
                        prompt.append(a.getName().charAt(i));
                    }
                }
                prompt.append(",");
            }
        }

        if (topGenresList != null) {
            prompt.append("Here are the users top genres: ");
            for (String a : topGenresList) {
                for (int i = 0; i < a.length(); i++) {
                    if (a.charAt(i) =='"') {
                        prompt.append("'");
                    } else {
                        prompt.append(a.charAt(i));
                    }
                }
                prompt.append(", ");
            }
        }

        if (topTracksList != null) {
            prompt.append("Here are the users top tracks: ");
            for (Track a : topTracksList) {
                for (int i = 0; i < a.getTrackName().length(); i++) {
                    if (a.getTrackName().charAt(i) =='"') {
                        prompt.append("'");
                    } else {
                        prompt.append(a.getTrackName().charAt(i));
                    }
                }
                prompt.append(", ");
            }
        }

        currentUser.getUserWraps().remove(String.valueOf(currentUser.getUserWraps().size() - 1));
        List<Insight> dummyInsights = new ArrayList<>();
        dummyInsights.add(new Insight("Loading...", "Your insights will appear here soon..."));
        RecyclerView recyclerView = view.findViewById(R.id.insights_recycler_view);
        InsightsAdapter adapter = new InsightsAdapter(dummyInsights);
        recyclerView.setAdapter(adapter);

        LLMQueryManager manager = new LLMQueryManager();
        HandlerThread thread = new HandlerThread("API_CALL");
        thread.start();
        Handler requestHandler = new Handler(thread.getLooper());
        final Handler responseHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<String> preferences = (ArrayList<String>) msg.obj;
                List<Insight> insights = new ArrayList<>();
                for (int i = 0; i < Math.min(preferences.size(), 4); i++) {
                    String title = "Insight #" + (i + 1);
                    String description = preferences.get(i);
                    insights.add(new Insight(title, description));
                }

                RecyclerView recyclerView = view.findViewById(R.id.insights_recycler_view);
                InsightsAdapter adapter = new InsightsAdapter(insights);
                recyclerView.setAdapter(adapter);
            }
        };

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Response reply = manager.queryPrompt(prompt.toString());
                    ResponseBody replyBody = reply.body();
                    assert replyBody != null;
                    String res = replyBody.string();
                    JSONObject replyJson = new JSONObject(res);
                    String output = replyJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                    JSONObject jsonOutput = new JSONObject(output);
                    JSONArray out = jsonOutput.getJSONArray("preferences");
                    Message msg = new Message();
                    ArrayList<String> preferences = new ArrayList<>();
                    for (int i = 0; i < out.length(); i++) {
                        preferences.add(out.getString(i));
                    }
                    msg.obj = preferences;
                    responseHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        requestHandler.post(myRunnable);
    }
}