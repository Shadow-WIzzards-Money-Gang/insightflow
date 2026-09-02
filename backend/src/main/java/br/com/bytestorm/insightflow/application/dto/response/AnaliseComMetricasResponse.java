package br.com.bytestorm.insightflow.application.dto.response;

import org.springframework.data.domain.Page;

public record AnaliseComMetricasResponse(
    MetricasResponse metricas,
    Page<AnaliseResponse> analises
) {

}
