package com.bytestorm.insightflow.infra.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bytestorm.insightflow.config.ApplicationProperties;
import com.bytestorm.insightflow.domain.exceptions.ai.ApiUrlNaoEncontradaException;
import com.bytestorm.insightflow.domain.exceptions.ai.ChaveApiNaoEncontradaException;
import com.bytestorm.insightflow.domain.exceptions.ai.ChaveDeApiInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.ai.ErroInesperadoApiException;
import com.bytestorm.insightflow.domain.exceptions.ai.ErroInternoDaApiException;
import com.bytestorm.insightflow.domain.exceptions.ai.InstrucaoInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.ai.LimiteDeRequisicoesExcedidoException;
import com.bytestorm.insightflow.domain.exceptions.ai.ModeloIaNaoEncontradoException;
import com.bytestorm.insightflow.domain.exceptions.ai.PromptInvalidoException;
import com.bytestorm.insightflow.domain.exceptions.ai.RespostaInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.ai.TemperaturaInvalidaException;
import com.bytestorm.insightflow.domain.interfaces.AiClient;

import java.io.IOException;

public class GroqClient implements AiClient {

    private static GroqClient instance;

    private final String MODEL_NAME;
    private final String API_URL;
    private final String API_KEY;
    private final Double TEMPERATURE;

    private final OkHttpClient client;

    private final String instruction;

    public GroqClient(String instruction) {

        this.API_URL = ApplicationProperties.getInstance().get("ai.url");
        this.MODEL_NAME = ApplicationProperties.getInstance().get("ai.model");
        this.API_KEY = ApplicationProperties.getInstance().get("ai.api.key");
        this.TEMPERATURE = Double.parseDouble(ApplicationProperties.getInstance().get("ai.temperature"));

        this.instruction = instruction;

        this.validarConfiguracao();
        this.client = new OkHttpClient();
    }

    private void validarConfiguracao() {
        if (this.API_URL == null || this.API_URL.isBlank()) {
            throw new ApiUrlNaoEncontradaException();
        }
        
        if (this.API_KEY == null || this.API_KEY.isEmpty()) {
            throw new ChaveApiNaoEncontradaException();
        }

        if (this.MODEL_NAME == null || this.MODEL_NAME.isBlank()) {
            throw new ModeloIaNaoEncontradoException();
        }

        if (this.TEMPERATURE == null) {
            throw new TemperaturaInvalidaException();
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

    @Override
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
                .put("temperature", TEMPERATURE)
                .put("messages", messages);
    }
}