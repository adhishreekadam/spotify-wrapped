package com.example.spotify_wrapped;


import com.example.spotify_wrapped.User;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.android.gms.tasks.OnCompleteListener;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;








import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.HashMap;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SpotifyAuthenticator {
    public static final String CLIENT_ID = "5f05cbeda0b94c5fa48f4c6a1b5e56cd";
    public static final String REDIRECT_URI = "spotify-wrapped://auth";
    private Call call;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
    private final DatabaseReference idHash = FirebaseDatabase.getInstance().getReference("id_hash");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private UserViewModel userViewModel;
    private String userId;


    public SpotifyAuthenticator(UserViewModel userViewModel) {
        this.userViewModel = userViewModel;
    }






    public void getToken(Activity context) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(context, 0, request);
    }


    public void trySpotifyLogin(Activity context) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(context, 1, request);
    }
    private void confirmLogin(Activity context) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(context, 2, request);
    }


    public void confirm(Activity context, String accessToken) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("HTTP", "Login failed");
            }


            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String id = (String) jsonObject.get("id");
                    idHash.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.getResult() != null) {
                                String appId = (String) task.getResult().getValue();
                                if (appId.equals(userId)) {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("currentUserId", userId);
                                    intent.putExtra("accessToken", accessToken);
                                    context.setResult(-1, intent);
                                    context.finish();
                                } else {
                                    Toast.makeText(context, "Account is associated with a different spotify account", Toast.LENGTH_SHORT);
                                }
                            } else {
                                Toast.makeText(context, "Account is not connected to a spotify account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (JSONException e) {

                }


            }
        });
    }
    public void loginEmailPassword(Activity context, String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            userId = user.getUid();
                            confirmLogin(context);
                        } else {
//                            Exception exception = task.getException();
//                            if (exception != null) {
//                                Log.wtf("invalid credentials", exception.getMessage());
//                            }
                            Toast.makeText(context, "Invalid Credentials",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void authenticateWithSpotify(Activity context, String accessToken) {
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }


            @Override
            public void onResponse(Call call, Response response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String id = (String) jsonObject.get("id");
                    idHash.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, "Failed to authenticate Spotify account", Toast.LENGTH_SHORT).show();
                            } else {
                                if (task.getResult().getValue() == null) {
                                    Toast.makeText(context, "Spotify account is not linked to this app. Sign up to create an account", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Logging you in to existing account", Toast.LENGTH_SHORT).show();
                                    userId = (String) task.getResult().getValue();
                                    mDatabase.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            HashMap<String, Object> vals = (HashMap<String, Object>) task.getResult().getValue();
                                            String email = (String) vals.get("email");
                                            String password = (String) vals.get("password");
                                            mAuth.signInWithEmailAndPassword(email, password)
                                                    .addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                FirebaseUser user = mAuth.getCurrentUser();
                                                                userId = user.getUid();
                                                                Intent intent = new Intent(context, MainActivity.class);
                                                                intent.putExtra("currentUserId", userId);
                                                                intent.putExtra("accessToken", accessToken);
                                                                context.setResult(-1, intent);
                                                                context.finish();
                                                            } else {
                                                                Toast.makeText(context, "Invalid Credentials",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(context, "Failed to link to spotify", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            }
        });
    }


    public void updateAccessToken(Activity context) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(context, 1, request);
    }




    public void createNewUser(Activity context, String accessToken, String username, String password) throws IllegalArgumentException {
        if (username.equals("") || password.equals("") || password.length() < 6) {
            throw new IllegalArgumentException();
        }
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        call = okHttpClient.newCall(request);


        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(context, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String Spotid = (String) jsonObject.get("id");
                    idHash.child(Spotid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {
                                if ((task.getResult().getValue()) == null) {
                                    try {
                                        String name = (String) jsonObject.get("display_name");
                                        String email = (String) jsonObject.get("email");
                                        JSONArray imageArray = (JSONArray) jsonObject.get("images");
                                        String image;
                                        if (imageArray.isNull(0)) {
                                            image = null;
                                        } else {
                                            JSONObject imageOb = (JSONObject) imageArray.get(1);
                                            image = (String) imageOb.get("url");
                                        }
                                        mAuth.createUserWithEmailAndPassword(email,password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        String appID = mAuth.getCurrentUser().getUid();
                                                        userId = mAuth.getCurrentUser().getUid();
                                                        User newUser = new User(name, email, appID, image, password, username, accessToken, Spotid);
                                                        idHash.child(Spotid).setValue(appID);
                                                        mDatabase.child(appID).setValue(newUser);
                                                        mDatabase.child(appID).child("image").setValue(image);


                                                        Intent intent = new Intent(context, MainActivity.class);
                                                        intent.putExtra("currentUserId", appID);
                                                        intent.putExtra("accessToken", accessToken);
                                                        context.setResult(-1, intent);
                                                        context.finish();
                                                    }
                                                });
                                    } catch (JSONException e) {
                                    }


                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(context, "Signing you in to existing account", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    });
                                    String id = (String) task.getResult().getValue();


                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("currentUserId", id);
                                    context.setResult(-1, intent);
                                    context.finish();
                                }
                            }
                        }
                    });


                } catch (JSONException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(context, "Failed to Parse data", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });


                }
            }
        });


    }




    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }


    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }


    public String getUserId() {
        return userId;
    }
}
