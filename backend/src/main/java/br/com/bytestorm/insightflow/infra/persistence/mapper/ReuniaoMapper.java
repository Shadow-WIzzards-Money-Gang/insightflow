package br.com.bytestorm.insightflow.infra.persistence.mapper;

import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.vo.HashTranscricao;
import br.com.bytestorm.insightflow.infra.persistence.entity.ReuniaoJpaEntity;
import br.com.bytestorm.insightflow.infra.persistence.entity.SegmentoClienteJpaEntity;

public final class ReuniaoMapper {

    private ReuniaoMapper() {
    }

    /** Le o segmento (associacao lazy): precisa ser chamado com a sessao aberta. */
    public static Reuniao toDomain(ReuniaoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Reuniao.reconstituir(
            entity.getId(),
            entity.getTranscricaoBruta(),
            new HashTranscricao(entity.getHashTranscricao()),
            entity.getDataReuniao(),
            entity.getDuracao(),
            SegmentoClienteMapper.toDomain(entity.getSegmentoCliente()),
            entity.getCreatedAt()
        );
    }

    public static ReuniaoJpaEntity toJpa(Reuniao reuniao, SegmentoClienteJpaEntity segmentoCliente) {
        return ReuniaoJpaEntity.builder()
                .id(reuniao.getId())
                .transcricaoBruta(reuniao.getTranscricaoBruta())
                .hashTranscricao(reuniao.getHashTranscricao().valor())
                .dataReuniao(reuniao.getDataReuniao())
                .duracao(reuniao.getDuracao())
                .segmentoCliente(segmentoCliente)
                .build();
    }
}
