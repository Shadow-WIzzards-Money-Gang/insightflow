package br.com.bytestorm.insightflow.infra.ai;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import br.com.bytestorm.insightflow.application.dto.ia.AnaliseIAResult;

@Component
public class AiClient {

    private final ChatClient chatClient;
    private final String systemInstruction;

    public AiClient(ChatClient.Builder chatClientBuilder,
            @Value("classpath:ai/prompts/analise-reuniao-system.txt") Resource systemInstructionResource) throws IOException {
        this.chatClient = chatClientBuilder.build();
        this.systemInstruction = StreamUtils.copyToString(systemInstructionResource.getInputStream(), StandardCharsets.UTF_8);
    }

    public AnaliseIAResult analisarReuniao(String transcricaoBruta, String produtosDisponiveis) {
        return chatClient.prompt()
                .system(systemInstruction.formatted(produtosDisponiveis))
                .user(transcricaoBruta)
                .call()
                .entity(AnaliseIAResult.class);
    }

}
