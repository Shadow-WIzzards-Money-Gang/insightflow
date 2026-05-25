package com.bytestorm.insightflow.domain.valueobject;

public record AnaliseReuniaoDTO(
    String assunto,
    String sentimentoReuniao,
    String riscoCancelamento,
    String segmentoCliente
) {

}
