package br.com.bytestorm.insightflow.application.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.response.DistribuicaoRiscoResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasGlobais;
import br.com.bytestorm.insightflow.application.dto.response.MetricasResponse;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

/**
 * Monta o {@link MetricasResponse} a partir dos agregados ja calculados no banco
 * ({@link MetricasGlobais} + as duas distribuicoes de risco). A unica coisa feita
 * em memoria e a eleicao de produto/segmento mais critico ({@link #maisCritico}),
 * que roda sobre listas pequenas.
 */
@Service
public class MetricaService {

    public MetricasResponse montar(
        MetricasGlobais global,
        List<DistribuicaoRiscoResponse> riscoPorProduto,
        List<DistribuicaoRiscoResponse> riscoPorSegmento
    ) {
        return new MetricasResponse(
            global.totalReunioes(),
            global.muitoAlto(),
            global.alto(),
            global.moderado(),
            global.baixo(),
            calcularMediaSentimento(global),
            global.notaMedia(),
            global.sentPositivo(),
            global.sentNeutro(),
            global.sentNegativo(),
            riscoPorProduto,
            riscoPorSegmento,
            maisCritico(riscoPorProduto),
            maisCritico(riscoPorSegmento)
        );
    }

    DistribuicaoRiscoResponse maisCritico(List<DistribuicaoRiscoResponse> distribuicoes) {
        return distribuicoes.stream()
            .filter(d -> d.totalCritico() > 0)
            .max(Comparator.comparingLong(DistribuicaoRiscoResponse::scoreCriticidade)
                // desempate: quem tem o risco mais severo em maior quantidade
                .thenComparingLong(DistribuicaoRiscoResponse::muitoAlto)
                .thenComparingLong(DistribuicaoRiscoResponse::alto)
                .thenComparingLong(DistribuicaoRiscoResponse::moderado)
                .thenComparingLong(DistribuicaoRiscoResponse::total)
                .thenComparing(Comparator.comparing(DistribuicaoRiscoResponse::rotulo).reversed()))
            .orElse(null);
    }

    private SentimentoReuniao calcularMediaSentimento(MetricasGlobais global) {
        long positivos = valorOuZero(global.sentPositivo());
        long neutros = valorOuZero(global.sentNeutro());
        long negativos = valorOuZero(global.sentNegativo());
        long total = positivos + neutros + negativos;

        if (total == 0) {
            return SentimentoReuniao.fromValor(0.0);
        }

        double somaValores = (positivos * SentimentoReuniao.POSITIVO.getValor())
            + (neutros * SentimentoReuniao.NEUTRO.getValor())
            + (negativos * SentimentoReuniao.NEGATIVO.getValor());

        return SentimentoReuniao.fromValor(somaValores / total);
    }

    private static long valorOuZero(Long valor) {
        return valor == null ? 0L : valor;
    }
}
