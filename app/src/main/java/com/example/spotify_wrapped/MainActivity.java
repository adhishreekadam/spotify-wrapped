package com.example.spotify_wrapped;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.spotify_wrapped.databinding.ActivityMainBinding;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "396b2e0e8f1544a98b0d8ffd88484dfd";
    public static final String REDIRECT_URI = "spotify-wrapped://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 2;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;

    private ActivityMainBinding binding;

    private UserViewModel model;

    private TextView tokenTextView, codeTextView, profileTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent login = new Intent(this, LoginActivity.class);
        MainActivity.this.startActivityForResult(login, LOGIN_ACTIVITY_REQUEST_CODE);

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setVisibility(View.VISIBLE);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_insights) {
                replaceFragment(new InsightsFragment());
            } else if (itemId == R.id.navigation_home) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.add_wrap) {
                replaceFragment(new ChooseTimeWrapFragment());
            }

            return true;
        });

        // Initialize the views
//        tokenTextView = (TextView) findViewById(R.id.token_text_view);
//        codeTextView = (TextView) findViewById(R.id.code_text_view);
//        profileTextView = (TextView) findViewById(R.id.response_text_view);
//
//        // Initialize the buttons
//        Button tokenBtn = (Button) findViewById(R.id.token_btn);
//        Button codeBtn = (Button) findViewById(R.id.code_btn);
//        Button profileBtn = (Button) findViewById(R.id.profile_btn);
//
//        // Set the click listeners for the buttons
//
//        tokenBtn.setOnClickListener((v) -> {
//            getToken();
//        });
//
//        codeBtn.setOnClickListener((v) -> {
//            getCode();
//        });
//
//        profileBtn.setOnClickListener((v) -> {
//            onGetUserProfileClicked();
//        });
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    private void getToken() {
        AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    private void getCode() {
        AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == AUTH_TOKEN_REQUEST_CODE || requestCode == AUTH_CODE_REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            if (response.getError() != null && response.getError().isEmpty()) {
                if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
                    mAccessToken = response.getAccessToken();
                    setTextAsync(mAccessToken, tokenTextView);
                } else if (requestCode == AUTH_CODE_REQUEST_CODE) {
                    mAccessCode = response.getCode();
                    setTextAsync(mAccessCode, codeTextView);
                }
            } else {
                Toast.makeText(this, "Error: " + response.getError(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String id = intent.getStringExtra("currentUserId");
                String accessToken = intent.getStringExtra("accessToken");
                Log.wtf("huh", id);
                model = new ViewModelProvider(this).get(UserViewModel.class);
//                Thread t = new Thread(new Runnable() {
//                    public void run() {
//                        model.getUserInformation(id, accessToken);
//                        synchronized(model) {
//                            try {
//                                model.wait();
//                                System.out.println("hi");
//                            } catch (InterruptedException e){
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//                t.start();
                model.getUserInformationSynch(id, accessToken, this);


                System.out.println("FUCKCKC");
            }
        }
    }


    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        getRedirectUri();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, type, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private"});
        return builder.build();
    }


    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }


}
