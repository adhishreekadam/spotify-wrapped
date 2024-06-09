package com.example.spotify_wrapped;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateAccountActivity extends AppCompatActivity {
    private UserViewModel model;
    private EditText editName, editUsername, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("userId");
            String accessToken = intent.getStringExtra("accessToken");
            model = new ViewModelProvider(this).get(UserViewModel.class);
            model.getUserInformation(userId, accessToken);
            ImageButton backToSettingsBtn = findViewById(R.id.back_to_settings);
            backToSettingsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

        } else {
            Toast.makeText(this, "missing user ID or access token", Toast.LENGTH_SHORT).show();
            finish();
        }

        editName = findViewById(R.id.edit_name);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.update_password);
        Button saveChangesBtn = findViewById(R.id.save_changes);

        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString().trim();
                String newUsername = editUsername.getText().toString().trim();
                String newPassword = editPassword.getText().toString().trim();
                User currentUser = model.getCurrentUser();

                if (currentUser != null) {
                    User updatedUser = new User(newName, currentUser.getEmail(), currentUser.getId(),
                            currentUser.getImage(), newPassword, newUsername, currentUser.getAccessToken(),
                            currentUser.getSpotId());
                    model.updateUserInformation(updatedUser, UpdateAccountActivity.this);
                } else {
                    Toast.makeText(UpdateAccountActivity.this, "data not available", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

    }

}

