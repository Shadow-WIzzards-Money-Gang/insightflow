package br.com.bytestorm.insightflow.infra.persistence.mapper;

import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.infra.persistence.entity.SegmentoClienteJpaEntity;

public final class SegmentoClienteMapper {

    private SegmentoClienteMapper() {
    }

    public static SegmentoCliente toDomain(SegmentoClienteJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return SegmentoCliente.reconstituir(entity.getId(), entity.getNome());
    }

    public static SegmentoClienteJpaEntity toJpa(SegmentoCliente segmentoCliente) {
        return SegmentoClienteJpaEntity.builder()
                .id(segmentoCliente.getId())
                .nome(segmentoCliente.getNome())
                .build();
    }
}
