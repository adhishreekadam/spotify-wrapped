package com.example.spotify_wrapped;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;


public class LoginActivity extends AppCompatActivity {

    private SpotifyAuthenticator authenticator;
    private String accessToken;
    private boolean linkedToSpotify = false;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        authenticator = new SpotifyAuthenticator(userViewModel);

        CardView cardViewSignIn = findViewById(R.id.cardview_sign_in);
        CardView cardViewSignUp = findViewById(R.id.cardview_sign_up);
        TextView signUpTextView = findViewById(R.id.sign_up_selection_clickable);
        TextView signInTextView = findViewById(R.id.sign_in_selection_clickable);
        EditText newUserName = findViewById(R.id.username_sign_up);
        EditText newPassword = findViewById(R.id.password_sign_up);
        Button linkSpotify = findViewById(R.id.linkSpotifyButton);
        Button submitSignUp = findViewById(R.id.signup_button);
        Button logInWithSpotify = findViewById(R.id.sign_in_with_spotify);
        EditText loginEmail = findViewById(R.id.email_sign_in);
        EditText loginPassword = findViewById(R.id.password_sign_in);
        Button submitLogin = findViewById(R.id.sign_in_button);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewSignIn.setVisibility(View.GONE);
                cardViewSignUp.setVisibility(View.VISIBLE);
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardViewSignIn.setVisibility(View.VISIBLE);
                cardViewSignUp.setVisibility(View.GONE);
            }
        });

        linkSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticator.getToken(LoginActivity.this);
            }
        });
        logInWithSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticator.trySpotifyLogin(LoginActivity.this);
            }
        });

        submitSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linkedToSpotify) {
                    String username = newUserName.getText().toString();
                    String password = newPassword.getText().toString();
                    try {
                        authenticator.createNewUser(LoginActivity.this, accessToken, username, password);
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(LoginActivity.this, "Please fill out all fields. Password must be more than 6 characters",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Please fill out all fields. Password must be more than 6 characters",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please connect to your Spotify account",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        submitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                authenticator.loginEmailPassword(LoginActivity.this, email, password);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (requestCode == 0) {
            accessToken = response.getAccessToken();
            if (accessToken != null) {
                linkedToSpotify = true;
            }
        } else if (requestCode == 1) {
            accessToken = response.getAccessToken();
            if (accessToken != null) {
                authenticator.authenticateWithSpotify(LoginActivity.this, accessToken);
            }
        } else if (requestCode == 2) {
            accessToken = response.getAccessToken();
            if (accessToken != null) {
                authenticator.confirm(this, accessToken);
            }
        }
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        String id = authenticator.getUserId();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currentUserId", id);
        setResult(RESULT_OK, intent);
//        startActivity(intent);
//        String id2 = null;
//        if (getIntent() != null && getIntent().getExtras() != null) {
//            id2 = getIntent().getExtras().getString("currentUserId");
//        }
//        Log.e("id", id2 != null ? id2 : "id2 is null");
    }
}
