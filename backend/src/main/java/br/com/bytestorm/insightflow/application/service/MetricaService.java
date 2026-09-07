package br.com.bytestorm.insightflow.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.response.DistribuicaoRiscoResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasResponse;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

@Service
public class MetricaService {

    public MetricasResponse calcularMetricas(List<AnaliseReuniao> analises) {
        List<DistribuicaoRiscoResponse> riscoPorProduto = distribuirRiscoPorProduto(analises);
        List<DistribuicaoRiscoResponse> riscoPorSegmento = distribuirRiscoPorSegmento(analises);

        return new MetricasResponse(
            (long) analises.size(),
            contarPorRisco(analises, RiscoCancelamento.MUITO_ALTO),
            contarPorRisco(analises, RiscoCancelamento.ALTO),
            contarPorRisco(analises, RiscoCancelamento.MODERADO),
            contarPorRisco(analises, RiscoCancelamento.BAIXO),
            calcularMediaSentimento(analises),
            calcularMediaNota(analises),
            contarPorSentimento(analises, SentimentoReuniao.POSITIVO),
            contarPorSentimento(analises, SentimentoReuniao.NEUTRO),
            contarPorSentimento(analises, SentimentoReuniao.NEGATIVO),
            riscoPorProduto,
            riscoPorSegmento,
            maisCritico(riscoPorProduto),
            maisCritico(riscoPorSegmento)
        );
    }

    private DistribuicaoRiscoResponse maisCritico(List<DistribuicaoRiscoResponse> distribuicoes) {
        return distribuicoes.stream()
            .filter(d -> d.totalCritico() > 0)
            .max(Comparator.comparingLong(DistribuicaoRiscoResponse::totalCritico)
                .thenComparingLong(DistribuicaoRiscoResponse::total)
                .thenComparing(Comparator.comparing(DistribuicaoRiscoResponse::rotulo).reversed()))
            .orElse(null);
    }

    private List<DistribuicaoRiscoResponse> distribuirRiscoPorProduto(List<AnaliseReuniao> analises) {
        Map<String, List<AnaliseReuniao>> porProduto = analises.stream()
            .filter(a -> a.getProdutoTotvs() != null)
            .collect(Collectors.groupingBy(a -> a.getProdutoTotvs().getNome()));

        return distribuirRisco(porProduto);
    }

    private List<DistribuicaoRiscoResponse> distribuirRiscoPorSegmento(List<AnaliseReuniao> analises) {
        Map<String, List<AnaliseReuniao>> porSegmento = analises.stream()
            .filter(a -> a.getReuniao() != null && a.getReuniao().getSegmentoCliente() != null)
            .collect(Collectors.groupingBy(a -> a.getReuniao().getSegmentoCliente().getNome()));

        return distribuirRisco(porSegmento);
    }

    private List<DistribuicaoRiscoResponse> distribuirRisco(Map<String, List<AnaliseReuniao>> grupos) {
        return grupos.entrySet().stream()
            .map(entrada -> {
                List<AnaliseReuniao> lista = entrada.getValue();
                return new DistribuicaoRiscoResponse(
                    entrada.getKey(),
                    (long) lista.size(),
                    contarPorRisco(lista, RiscoCancelamento.MUITO_ALTO),
                    contarPorRisco(lista, RiscoCancelamento.ALTO),
                    contarPorRisco(lista, RiscoCancelamento.MODERADO),
                    contarPorRisco(lista, RiscoCancelamento.BAIXO)
                );
            })
            .sorted(Comparator.comparingLong(DistribuicaoRiscoResponse::total).reversed()
                .thenComparing(DistribuicaoRiscoResponse::rotulo))
            .toList();
    }

    private Long contarPorRisco(List<AnaliseReuniao> analises, RiscoCancelamento risco) {
        return analises.stream()
            .filter(a -> a.getRiscoCancelamento() == risco)
            .count();
    }

    private Long contarPorSentimento(List<AnaliseReuniao> analises, SentimentoReuniao sentimento) {
        return analises.stream()
            .filter(a -> a.getSentimentoReuniao() == sentimento)
            .count();
    }

    private SentimentoReuniao calcularMediaSentimento(List<AnaliseReuniao> analises) {
        long positivos = contarPorSentimento(analises, SentimentoReuniao.POSITIVO);
        long neutros = contarPorSentimento(analises, SentimentoReuniao.NEUTRO);
        long negativos = contarPorSentimento(analises, SentimentoReuniao.NEGATIVO);
        long total = positivos + neutros + negativos;

        if (total == 0) {
            return SentimentoReuniao.fromValor(0.0);
        }

        double somaValores = (positivos * SentimentoReuniao.POSITIVO.getValor())
            + (neutros * SentimentoReuniao.NEUTRO.getValor())
            + (negativos * SentimentoReuniao.NEGATIVO.getValor());

        return SentimentoReuniao.fromValor(somaValores / total);
    }

    private Double calcularMediaNota(List<AnaliseReuniao> analises) {
        return analises.stream()
            .map(AnaliseReuniao::getNota)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }
}
