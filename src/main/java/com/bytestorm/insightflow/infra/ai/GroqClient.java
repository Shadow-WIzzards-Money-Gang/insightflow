package com.bytestorm.insightflow.infra.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bytestorm.insightflow.domain.exceptions.ai.ChaveApiNaoEncontradaException;
import com.bytestorm.insightflow.domain.exceptions.ai.ChaveDeApiInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.ai.ErroInesperadoApiException;
import com.bytestorm.insightflow.domain.exceptions.ai.ErroInternoDaApiException;
import com.bytestorm.insightflow.domain.exceptions.ai.InstrucaoInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.ai.LimiteDeRequisicoesExcedidoException;
import com.bytestorm.insightflow.domain.exceptions.ai.PromptInvalidoException;
import com.bytestorm.insightflow.domain.exceptions.ai.RespostaInvalidaException;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;

public class GroqClient {

    private static GroqClient instance;

    private final String MODEL_NAME = "llama-3.3-70b-versatile";
    private final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private final String API_KEY;

    private final OkHttpClient client;

    private final String instruction;

    public GroqClient(String instruction) {
        Dotenv dotenv = Dotenv.load();

        this.API_KEY = dotenv.get("GROQ_API_KEY");
        this.instruction = instruction;

        this.validarConfiguracao();
        this.client = new OkHttpClient();
    }

    private void validarConfiguracao() {
        if (this.API_KEY == null || this.API_KEY.isEmpty()) {
            throw new ChaveApiNaoEncontradaException();
        }

        if (this.instruction == null || this.instruction.isBlank()) {
            throw new InstrucaoInvalidaException();
        }
    }

    public static GroqClient init(String instruction) {
        if (instance == null) {
            instance = new GroqClient(instruction);
        }
        return instance;
    }

    public String generateContent(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new PromptInvalidoException();
        }

        try {
            JSONObject bodyJson = this.montarBody(prompt);

            RequestBody body = RequestBody.create(
                    bodyJson.toString(),
                    MediaType.get("application/json")
            );

            Request request = new Request.Builder()
                    .url(this.API_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute();) {
                if (response.body() == null) {
                    throw new RespostaInvalidaException();
                }

                String responseBody = response.body().string();

                if (!response.isSuccessful()) {
                    this.tratarErroHttp(response.code(), responseBody);
                }

                return extrairConteudo(responseBody);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String extrairConteudo(String responseBody) {
        JSONObject json = new JSONObject(responseBody);

        return json
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

    private void tratarErroHttp(Integer statusCode, String responseBody) {
        switch (statusCode) {
            case 401:
                throw new ChaveDeApiInvalidaException();
            case 429:
                throw new LimiteDeRequisicoesExcedidoException();
            case 500:
                throw new ErroInternoDaApiException();
            default:
                throw new ErroInesperadoApiException(statusCode, responseBody);
        }
    }

    private JSONObject montarBody(String prompt) {
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
        
        return new JSONObject()
                .put("model", MODEL_NAME)
                .put("temperature", 0.1)
                .put("messages", messages);
    }
}