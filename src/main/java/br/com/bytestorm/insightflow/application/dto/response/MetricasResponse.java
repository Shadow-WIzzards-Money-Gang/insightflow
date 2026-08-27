package br.com.bytestorm.insightflow.application.dto.response;

import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

public record MetricasResponse(
        Long totalReunioes,
        Long totalRiscoMuitoAlto,
        Long totalRiscoAlto,
        Long totalRiscoModerado,
        Long totalRiscoBaixo,
        SentimentoReuniao sentimentoMedio,
        Double notaMedia
) {

}
