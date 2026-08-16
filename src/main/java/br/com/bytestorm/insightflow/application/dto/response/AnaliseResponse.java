package br.com.bytestorm.insightflow.application.dto.response;

import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

public record AnaliseResponse(
    Long id,
    String assunto,
    String pontosPositivos,
    String pontosNegativos,
    Integer nota,
    ProdutoTotvsResponse produtoTotvs,
    SentimentoReuniao sentimentoReuniao,
    RiscoCancelamento riscoCancelamento,
    ReuniaoResponse reuniao
) {

    public static AnaliseResponse fromEntity(AnaliseReuniao analiseReuniao) {
        return new AnaliseResponse(
            analiseReuniao.getId(),
            analiseReuniao.getAssunto(),
            analiseReuniao.getPontosPositivos(),
            analiseReuniao.getPontosNegativos(),
            analiseReuniao.getNota(),
            ProdutoTotvsResponse.fromEntity(analiseReuniao.getProdutoTotvs()),
            analiseReuniao.getSentimentoReuniao(),
            analiseReuniao.getRiscoCancelamento(),
            ReuniaoResponse.fromEntity(analiseReuniao.getReuniao())
        );
    }

}
