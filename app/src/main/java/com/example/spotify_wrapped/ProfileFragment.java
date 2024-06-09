package com.example.spotify_wrapped;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private PastWrapsAdapter pastWrapsAdapter;
    private TextView profileName;
    private TextView profileUsername;
    private UserViewModel model;
    private UpdateAccountActivity updateUser;

    private final DatabaseReference user = FirebaseDatabase.getInstance().getReference("users");

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);
        model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

//        RecyclerView playlistsRecyclerView = view.findViewById(R.id.playlists_recycler_view);
//        List<Playlist> playlistsList = new ArrayList<>();
//        playlistsList.add(new Playlist("Playlist 1", "Image URL 1", null, new ArrayList<>()));
//        PlaylistsAdapter playlistsAdapter = new PlaylistsAdapter(getContext(), playlistsList);
//        playlistsRecyclerView.setAdapter(playlistsAdapter);

//        RecyclerView followingRecyclerView = view.findViewById(R.id.following_recycler_view);
//        List<Following> followingList = new ArrayList<>();
//        followingList.add(new Following("Artist 1", "Image URL 1"));
//        FollowingAdapter followingAdapter = new FollowingAdapter(getContext(), followingList);
//        followingRecyclerView.setAdapter(followingAdapter);

        GridView pastWraps = view.findViewById(R.id.past_wraps_grid);
        pastWrapsAdapter = new PastWrapsAdapter(getActivity(), model.getCurrentUser().getUserWraps(), getFragmentManager());
        pastWraps.setAdapter(pastWrapsAdapter);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        View settingsButton = view.findViewById(R.id.settings_button);

        ImageView profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileUsername = view.findViewById(R.id.profile_username);

        User currentUser = model.getCurrentUser();

        String currentUserImage = currentUser.getImage();
        String currentName = currentUser.getName();
        String currentUsername = currentUser.getUsername();

        profileName.setText(currentName);
        profileUsername.setText("@"+currentUsername);

        model.getCurrentUserLiveData().observe(getViewLifecycleOwner(), user -> {
            String updatedCurrentName = user.getName();
            String updatedUsername = user.getUsername();
            profileName.setText(updatedCurrentName);
            profileUsername.setText("@"+updatedUsername);
        });

//        profileName.setText(currentUserName);
//        profileUsername.setText("@"+currentUserUsername);
        WrapFragment.DownloadImageFromInternet downloadImageFromInternet = (WrapFragment.DownloadImageFromInternet) new WrapFragment.DownloadImageFromInternet(profileImage, getActivity()).execute(currentUserImage);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new SettingsFragment());
            }
        });

//        wrapButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new ChooseTimeWrapFragment());
//            }
//        });

//        pastWrapsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new PastWrapsFragment());
//            }
//        });

    }

    public void onDataChange(@NonNull DataSnapshot snapshot) {
        User updatedUser = snapshot.getValue(User.class);
        if (updatedUser != null) {
            // Update UI with the latest data fetched from Firebase
            String updatedCurrentName = updatedUser.getName();
            String updatedUsername = updatedUser.getUsername();
            profileName.setText(updatedCurrentName);
            profileUsername.setText("@" + updatedUsername);

        }
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}