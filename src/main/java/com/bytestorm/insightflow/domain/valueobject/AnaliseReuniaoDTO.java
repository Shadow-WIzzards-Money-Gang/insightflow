package com.bytestorm.insightflow.domain.valueobject;

import com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import com.bytestorm.insightflow.domain.entity.Reuniao;

public record AnaliseReuniaoDTO(
    String assunto,
    String sentimentoReuniao,
    String riscoCancelamento,
    String segmentoCliente,
    String produtoTotvs
) {

    public AnaliseReuniao toEntity(Reuniao reuniao) {
        return new AnaliseReuniao(this, reuniao);
    }
}
