package br.com.bytestorm.insightflow.infra.persistence.mapper;

import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.infra.persistence.entity.AnaliseReuniaoJpaEntity;
import br.com.bytestorm.insightflow.infra.persistence.entity.ProdutoTotvsJpaEntity;
import br.com.bytestorm.insightflow.infra.persistence.entity.ReuniaoJpaEntity;

public final class AnaliseReuniaoMapper {

    private AnaliseReuniaoMapper() {
    }

    /** Le reuniao e produto (associacoes lazy): precisa ser chamado com a sessao aberta. */
    public static AnaliseReuniao toDomain(AnaliseReuniaoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return AnaliseReuniao.reconstituir(
            entity.getId(),
            entity.getAssunto(),
            entity.getPontosPositivos(),
            entity.getPontosNegativos(),
            entity.getNota(),
            ReuniaoMapper.toDomain(entity.getReuniao()),
            ProdutoTotvsMapper.toDomain(entity.getProdutoTotvs()),
            entity.getSentimentoReuniao(),
            entity.getRiscoCancelamento(),
            entity.getMotivoCancelamento()
        );
    }

    public static AnaliseReuniaoJpaEntity toJpa(
        AnaliseReuniao analiseReuniao, ReuniaoJpaEntity reuniao, ProdutoTotvsJpaEntity produtoTotvs
    ) {
        return AnaliseReuniaoJpaEntity.builder()
                .id(analiseReuniao.getId())
                .assunto(analiseReuniao.getAssunto())
                .pontosPositivos(analiseReuniao.getPontosPositivos())
                .pontosNegativos(analiseReuniao.getPontosNegativos())
                .nota(analiseReuniao.getNota())
                .reuniao(reuniao)
                .produtoTotvs(produtoTotvs)
                .sentimentoReuniao(analiseReuniao.getSentimentoReuniao())
                .riscoCancelamento(analiseReuniao.getRiscoCancelamento())
                .motivoCancelamento(analiseReuniao.getMotivoCancelamento())
                .build();
    }
}
