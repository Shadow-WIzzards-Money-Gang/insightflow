package com.bytestorm.insightflow.application.service;

import com.bytestorm.insightflow.domain.valueobject.AnaliseReuniaoDTO;
import com.bytestorm.insightflow.infra.ai.GeminiClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AnalisarReuniaoService {
    private static final Integer DURACAO_MINIMA = 10; // minutos
    private static final String SYSTEM_PROMPT = """
            Você é um analisador de reuniões comerciais corporativas.

            Sua função é analisar transcrições de reuniões e identificar:

            - assunto principal da reunião
            - sentimento geral
            - risco de cancelamento do cliente
            - segmento do cliente

            Você DEVE retornar APENAS JSON válido.

            Nunca explique.
            Nunca escreva markdown.
            Nunca escreva texto adicional.
            Nunca use crases.
            Nunca use comentários.

            Os valores permitidos são:

            SentimentoReuniao:
            - POSITIVO
            - NEUTRO
            - NEGATIVO

            RiscoCancelamento:
            - MUITO_ALTO
            - ALTO
            - MODERADO
            - BAIXO

            SegmentoCliente:
            - SAUDE
            - DISTRIBUICAO
            - MANUFATURA
            - SERVICOS
            - CONSTRUCAO_E_PROJETOS
            - EDUCACIONAL
            - AGROINDUSTRIA
            - LOGISTICA
            - SUPERMERCADOS
            - VAREJO
            - NAO_IDENTIFICADO

            O JSON deve seguir EXATAMENTE este formato:

            {
            "assunto": "...",
            "sentimentoReuniao": "...",
            "riscoCancelamento": "...",
            "segmentoCliente": "..."
            }""";

    private static GeminiClient geminiClient = GeminiClient.init(SYSTEM_PROMPT);

    public static AnaliseReuniaoDTO analisarTranscricao(String transcricao, Integer duracao) throws JsonMappingException, JsonProcessingException {
        if (duracao < DURACAO_MINIMA) {
            throw new IllegalArgumentException("A reunião é muito curta para análise.");
        }

        String analise = geminiClient.generateContent(
            """
            Analise a seguinte reunião comercial e identifique:

            - assunto principal
            - sentimento geral
            - risco de cancelamento
            - segmento do cliente

            TRANSCRIÇÃO:

            %s

            """.formatted(transcricao)
        );

            return parseAnalise(analise);
    } 

    private static AnaliseReuniaoDTO parseAnalise(String texto) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        AnaliseReuniaoDTO analise =
            mapper.readValue(
                texto,
                AnaliseReuniaoDTO.class
            );
        return analise;
    }
}
