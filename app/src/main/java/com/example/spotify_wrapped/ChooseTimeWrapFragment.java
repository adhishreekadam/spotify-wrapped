package com.example.spotify_wrapped;

import static com.example.spotify_wrapped.UserItemTimeFrame.LONG;
import static com.example.spotify_wrapped.UserItemTimeFrame.MEDIUM;
import static com.example.spotify_wrapped.UserItemTimeFrame.SHORT;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChooseTimeWrapFragment extends Fragment {

    UserViewModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_time_wrap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        View wrapShortButton = view.findViewById(R.id.wrap_button_short);
        View wrapMediumButton = view.findViewById(R.id.wrap_button_medium);
        View wrapLongButton = view.findViewById(R.id.wrap_button_long);

        WrapFragment wrapFragment = new WrapFragment();
        Bundle bundle = new Bundle();


        wrapShortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("timeChoice", "1");
                wrapFragment.setArguments(bundle);
                replaceFragment(wrapFragment);
            }
        });

        wrapMediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("timeChoice", "2");
                wrapFragment.setArguments(bundle);
                replaceFragment(wrapFragment);
            }
        });

        wrapLongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("timeChoice", "3");
                wrapFragment.setArguments(bundle);
                replaceFragment(wrapFragment);
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}