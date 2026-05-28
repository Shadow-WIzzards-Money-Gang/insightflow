package com.bytestorm.insightflow.domain.valueobject;

import com.bytestorm.insightflow.domain.entity.AnaliseReuniao;

public record AnaliseReuniaoDTO(
    String assunto,
    String sentimentoReuniao,
    String riscoCancelamento,
    String segmentoCliente
) {

    public AnaliseReuniao toEntity() {
        return new AnaliseReuniao(this);
    }

}
