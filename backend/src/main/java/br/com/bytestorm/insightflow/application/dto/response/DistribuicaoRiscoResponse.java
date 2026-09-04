package br.com.bytestorm.insightflow.application.dto.response;

public record DistribuicaoRiscoResponse(
    String rotulo,
    Long total,
    Long muitoAlto,
    Long alto,
    Long moderado,
    Long baixo
) {

}
