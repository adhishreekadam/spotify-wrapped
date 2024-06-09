package com.example.spotify_wrapped;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LLMQueryManager {
    public static String API_KEY;
    final OkHttpClient okHttpClient;

    private Call call;

    public LLMQueryManager() {
        okHttpClient = new OkHttpClient();
        API_KEY = BuildConfig.OPENAI_API_KEY;
    }

    public Response queryPrompt(String prompt) throws IOException {
        String body = "{" +
                "\"model\":\"gpt-3.5-turbo\", " +
                "\"response_format\": {\"type\": \"json_object\"}," +
                "\"messages\": [{\"role\":\"system\", \"content\": " +
                "\"You are a spotify assistant with the goal of describing users based on their music taste. " +
                "You will be given information about the user and should describe things like how they would act, think, and dress. " +
                "Return this information in a single array in JSON format named preferences, containing at least ten pieces of information. Respond by referring to the user in the second person. For example: 'you are a fan of mainstream artists.'\"}," +
                "{\"role\": \"user\", \"content\":\"" + prompt + "\"}]}";
        Log.d("Prompt", body);
        RequestBody reqBody = RequestBody.create(body, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(reqBody)
                .build();
        call = okHttpClient.newCall(request);

        return call.execute();
    }
}
