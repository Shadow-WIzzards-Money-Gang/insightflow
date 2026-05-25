package com.bytestorm.insightflow.infra.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class GroqClient {

    private static GroqClient instance;
    private final Dotenv dotenv = Dotenv.load();

    private final String MODEL_NAME = "llama-3.3-70b-versatile";
    private final String API_KEY = dotenv.get("GROQ_API_KEY");

    private final OkHttpClient client;

    private final String instruction;

    public GroqClient(String instruction) {
        this.client = new OkHttpClient();
        this.instruction = instruction;
    }

    public static GroqClient init(String instruction) {
        if (instance == null) {
            instance = new GroqClient(instruction);
        }
        return instance;
    }

    public String generateContent(String prompt) {

        try {

            JSONObject bodyJson = new JSONObject();

            bodyJson.put("model", MODEL_NAME);
            bodyJson.put("temperature", 0.1);

            JSONArray messages = new JSONArray();

            messages.put(
                new JSONObject()
                    .put("role", "system")
                    .put("content", instruction)
            );

            messages.put(
                new JSONObject()
                    .put("role", "user")
                    .put("content", prompt)
            );

            bodyJson.put("messages", messages);

            RequestBody body = RequestBody.create(
                    bodyJson.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IllegalArgumentException("Erro na chamada à API: " + response.code() + " - " + response);
            }

            String responseBody = response.body().string();

            JSONObject json = new JSONObject(responseBody);

            return json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}