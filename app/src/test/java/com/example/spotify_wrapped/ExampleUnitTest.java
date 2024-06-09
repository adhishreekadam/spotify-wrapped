package com.example.spotify_wrapped;

import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;

import okhttp3.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_llmQuery() throws IOException {
        LLMQueryManager manager = new LLMQueryManager();
        Response res = manager.queryPrompt("I love listening to Drake");

        System.out.println(res.body().string());

        assertEquals(res.code(), 200);
    }
}