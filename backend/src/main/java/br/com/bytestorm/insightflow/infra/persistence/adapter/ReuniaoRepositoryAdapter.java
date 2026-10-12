package br.com.bytestorm.insightflow.infra.persistence.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.repository.ReuniaoRepository;
import br.com.bytestorm.insightflow.domain.vo.HashTranscricao;
import br.com.bytestorm.insightflow.domain.vo.Pagina;
import br.com.bytestorm.insightflow.domain.vo.Paginacao;
import br.com.bytestorm.insightflow.infra.persistence.entity.ReuniaoJpaEntity;
import br.com.bytestorm.insightflow.infra.persistence.entity.SegmentoClienteJpaEntity;
import br.com.bytestorm.insightflow.infra.persistence.mapper.PaginacaoMapper;
import br.com.bytestorm.insightflow.infra.persistence.mapper.ReuniaoMapper;
import br.com.bytestorm.insightflow.infra.persistence.repository.ReuniaoJpaRepository;
import br.com.bytestorm.insightflow.infra.persistence.repository.SegmentoClienteJpaRepository;

@Repository
@Transactional(readOnly = true)
public class ReuniaoRepositoryAdapter implements ReuniaoRepository {

    private final ReuniaoJpaRepository jpaRepository;
    private final SegmentoClienteJpaRepository segmentoClienteJpaRepository;

    public ReuniaoRepositoryAdapter(
        ReuniaoJpaRepository jpaRepository, SegmentoClienteJpaRepository segmentoClienteJpaRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.segmentoClienteJpaRepository = segmentoClienteJpaRepository;
    }

    @Override
    @Transactional
    public Reuniao salvar(Reuniao reuniao) {
        SegmentoClienteJpaEntity segmentoCliente = reuniao.getSegmentoCliente() == null
            ? null
            : segmentoClienteJpaRepository.getReferenceById(reuniao.getSegmentoCliente().getId());

        return ReuniaoMapper.toDomain(jpaRepository.save(ReuniaoMapper.toJpa(reuniao, segmentoCliente)));
    }

    @Override
    public Optional<Reuniao> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ReuniaoMapper::toDomain);
    }

    @Override
    public Optional<Reuniao> buscarPorHashTranscricao(HashTranscricao hashTranscricao) {
        return jpaRepository.findByHashTranscricao(hashTranscricao.valor()).map(ReuniaoMapper::toDomain);
    }

    @Override
    public Pagina<Reuniao> buscarTodas(Paginacao paginacao) {
        Page<ReuniaoJpaEntity> pagina = jpaRepository.findAll(PaginacaoMapper.toPageable(paginacao));
        return new Pagina<>(
            pagina.getContent().stream().map(ReuniaoMapper::toDomain).toList(),
            pagina.getTotalElements()
        );
    }
}
