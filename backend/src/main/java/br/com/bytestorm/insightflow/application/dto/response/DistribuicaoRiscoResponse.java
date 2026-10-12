package br.com.bytestorm.insightflow.application.dto.response;

import br.com.bytestorm.insightflow.domain.vo.DistribuicaoRisco;

public record DistribuicaoRiscoResponse(
    String rotulo,
    Long total,
    Long muitoAlto,
    Long alto,
    Long moderado,
    Long baixo
) {

    public static DistribuicaoRiscoResponse from(DistribuicaoRisco distribuicao) {
        return new DistribuicaoRiscoResponse(
            distribuicao.rotulo(),
            distribuicao.total(),
            distribuicao.muitoAlto(),
            distribuicao.alto(),
            distribuicao.moderado(),
            distribuicao.baixo()
        );
    }
}
