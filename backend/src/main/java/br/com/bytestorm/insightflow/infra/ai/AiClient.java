package br.com.bytestorm.insightflow.infra.ai;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import br.com.bytestorm.insightflow.application.dto.ia.AnaliseIAResult;
import br.com.bytestorm.insightflow.domain.exceptions.ia.IAIndisponivelException;
import br.com.bytestorm.insightflow.domain.exceptions.ia.RespostaIAInvalidaException;
import tools.jackson.core.JacksonException;

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
        AnaliseIAResult resultado;

        try {
            resultado = chatClient.prompt()
                    .system(systemInstruction.formatted(produtosDisponiveis))
                    .user(transcricaoBruta)
                    .call()
                    .entity(AnaliseIAResult.class);
        } catch (RuntimeException e) {
            if (temCausaDeParsing(e)) {
                throw new RespostaIAInvalidaException(e);
            }
            throw new IAIndisponivelException(e);
        }

        validar(resultado);
        return resultado;
    }

    private void validar(AnaliseIAResult r) {
        if (r == null
                || isBlank(r.assunto())
                || isBlank(r.sentimentoReuniao())
                || isBlank(r.riscoCancelamento())
                || isBlank(r.produtoTotvsNome())
                || r.nota() == null) {
            throw new RespostaIAInvalidaException("A IA retornou uma analise incompleta.");
        }
        if (r.nota() < 0 || r.nota() > 10) {
            throw new RespostaIAInvalidaException("A IA retornou uma nota fora da faixa de 0 a 10: " + r.nota());
        }
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

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

}
