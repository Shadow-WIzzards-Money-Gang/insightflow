package br.com.bytestorm.insightflow.infra.ai;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import br.com.bytestorm.insightflow.domain.exceptions.ia.IAIndisponivelException;
import br.com.bytestorm.insightflow.domain.exceptions.ia.RespostaIAInvalidaException;
import br.com.bytestorm.insightflow.domain.gateway.AnalisadorReuniaoGateway;
import br.com.bytestorm.insightflow.domain.vo.ResultadoAnaliseIA;
import tools.jackson.core.JacksonException;

@Component
public class AiClient implements AnalisadorReuniaoGateway {

    private final ChatClient chatClient;
    private final String systemInstruction;

    public AiClient(ChatClient.Builder chatClientBuilder,
            @Value("classpath:ai/prompts/analise-reuniao-system.txt") Resource systemInstructionResource) throws IOException {
        this.chatClient = chatClientBuilder.build();
        this.systemInstruction = StreamUtils.copyToString(systemInstructionResource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Override
    public ResultadoAnaliseIA analisar(String transcricao, String produtosDisponiveis) {
        ResultadoAnaliseIA resultado;

        try {
            resultado = chatClient.prompt()
                    .system(systemInstruction.formatted(produtosDisponiveis))
                    .user(transcricao)
                    .call()
                    .entity(ResultadoAnaliseIA.class);
        } catch (RuntimeException e) {
            if (temCausaDeParsing(e)) {
                throw new RespostaIAInvalidaException(e);
            }
            throw new IAIndisponivelException(e);
        }

        if (resultado == null) {
            throw new RespostaIAInvalidaException("A IA retornou uma analise incompleta.");
        }
        resultado.validar();
        return resultado;
    }

    /** Falha ao desserializar a resposta do modelo (JSON invalido ou fora do formato esperado). */
    private boolean temCausaDeParsing(Throwable e) {
        for (Throwable atual = e; atual != null; atual = atual.getCause()) {
            if (atual instanceof JacksonException) {
                return true;
            }
        }
        return false;
    }

}
