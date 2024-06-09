package com.example.spotify_wrapped;

import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsFragment extends Fragment {

    private UserViewModel model;
    View deleteAccountBtn;
    //private User currentUser;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    View updateProfileBtn;
    private User currentUser;


    View settingsBackBtn;
    View darkLightBtn;
    private SharedPreferences sharedPreferences;
    private boolean isDarkModeEnabled;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        currentUser = model.getCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();

        currentUser = model.getCurrentUser();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateProfileBtn = view.findViewById(R.id.update_profile);
        deleteAccountBtn = view.findViewById(R.id.delete_account);
        settingsBackBtn = view.findViewById(R.id.settings_back_button);

        // on click listener for the button
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateAccountActivity.class);
                intent.putExtra("userId", currentUser.getId());
                intent.putExtra("accessToken", currentUser.getAccessToken());
                startActivity(intent);
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });

        settingsBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                model = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
                replaceFragment(new ProfileFragment());
            }
        });
        // Delete account button click listener
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

                builder.setMessage("Are you sure you want to delete your account?");

                builder.setTitle("Confirm Account Deletion");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User taps OK button, proceed with account deletion
                        model.deleteAccount(requireActivity());
                        // Navigate back to the login or registration screen
                        getActivity().recreate();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(SettingsFragmentViewModel.class);
//        // TODO: Use the ViewModel
//    }

}