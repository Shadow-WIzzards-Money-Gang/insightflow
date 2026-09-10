package br.com.bytestorm.insightflow.application.dto.response;

/**
 * Agregados globais das analises (uma unica linha), calculados no banco via
 * COUNT / conditional COUNT / AVG. Insumo para montar o {@link MetricasResponse}.
 */
public record MetricasGlobais(
    Long totalReunioes,
    Long muitoAlto,
    Long alto,
    Long moderado,
    Long baixo,
    Long sentPositivo,
    Long sentNeutro,
    Long sentNegativo,
    Double notaMedia
) {

    public long total() {
        return totalReunioes == null ? 0L : totalReunioes;
    }
}
