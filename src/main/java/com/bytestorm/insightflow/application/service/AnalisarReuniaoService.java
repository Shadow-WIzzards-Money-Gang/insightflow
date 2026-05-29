package com.bytestorm.insightflow.application.service;

import com.bytestorm.insightflow.domain.exceptions.analise.ReuniaoCurtaException;
import com.bytestorm.insightflow.infra.ai.GroqClient;

public class AnalisarReuniaoService {
    private static final Integer DURACAO_MINIMA = 10; // minutos
    private static final String SYSTEM_PROMPT = """
            Você é um analisador de reuniões comerciais corporativas.

            Sua função é analisar transcrições de reuniões e identificar:

            - assunto principal da reunião
            - sentimento geral
            - risco de cancelamento do cliente
            - segmento do cliente
            - produto totvs

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

            ProdutoTotvs:
            - PROTHEUS
            - RM
            - DATASUL
            - LOGIX
            - RH_CLOCK_IN
            - RH_PERFORMANCE
            - RD_STATION
            - TOTVS_ASSINATURA_ELETRONICA
            - TOTVS_ANALYTICS
            - FLUIG
            - TOTVS_AUTOMACAO_FISCAL
            - TOTVS_EDUCACIONAL
            - NAO_IDENTIFICADO

            O JSON deve seguir EXATAMENTE este formato:

            {
            "assunto": "...",
            "sentimentoReuniao": "...",
            "riscoCancelamento": "...",
            "segmentoCliente": "...",
            "produtoTotvs": "..."
            }""";

    private static GroqClient groqClient;

    private static GroqClient getGroqClient() {
        if (groqClient == null) {
            groqClient = GroqClient.init(SYSTEM_PROMPT);
        }
        return groqClient;
    }

    public static String analisarTranscricao(String transcricao, Integer duracao) {
        if (duracao < DURACAO_MINIMA) {
            throw new ReuniaoCurtaException();
        }

        String analise = getGroqClient().generateContent(
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

        return analise;
    } 
}
