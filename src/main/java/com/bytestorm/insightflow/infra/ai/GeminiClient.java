package com.bytestorm.insightflow.infra.ai;

import com.google.genai.types.Part;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

public class GeminiClient {
    private static GeminiClient instance;

    private final String MODEL_NAME = "gemini-2.0-flash";
    private final String API_KEY = "AIzaSyB7cJVShe96H0YmmDD90LgICifUkYmyZI8"; //System.getenv("GOOGLE_API_KEY");

    private final Client client;

    private final GenerateContentConfig config;

    public GeminiClient(String instruction) {
        this.client = Client.builder()
                .apiKey(this.API_KEY)
                .build();

        this.config = GenerateContentConfig.builder()
                .temperature(0.1f)
                .systemInstruction(Content.fromParts(Part.fromText(instruction)))
                .build();
    }

    public static GeminiClient init(String instruction) {
        if (instance == null) {
            instance = new GeminiClient(instruction);
        }
        return instance;
    }

    public String generateContent(String prompt) {
        GenerateContentResponse response =
            client.models.generateContent(
                this.MODEL_NAME,
                prompt,
                this.config
            );

        return response.text();
    }
}
