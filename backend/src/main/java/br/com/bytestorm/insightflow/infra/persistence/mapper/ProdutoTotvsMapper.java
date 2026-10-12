package br.com.bytestorm.insightflow.infra.persistence.mapper;

import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.infra.persistence.entity.ProdutoTotvsJpaEntity;

public final class ProdutoTotvsMapper {

    private ProdutoTotvsMapper() {
    }

    public static ProdutoTotvs toDomain(ProdutoTotvsJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ProdutoTotvs.reconstituir(entity.getId(), entity.getNome(), entity.getCategoria());
    }

    public static ProdutoTotvsJpaEntity toJpa(ProdutoTotvs produtoTotvs) {
        return ProdutoTotvsJpaEntity.builder()
                .id(produtoTotvs.getId())
                .nome(produtoTotvs.getNome())
                .categoria(produtoTotvs.getCategoria())
                .build();
    }
}
