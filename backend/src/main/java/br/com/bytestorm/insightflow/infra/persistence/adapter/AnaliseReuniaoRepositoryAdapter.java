package br.com.bytestorm.insightflow.infra.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.repository.AnaliseReuniaoRepository;
import br.com.bytestorm.insightflow.domain.vo.AnaliseExportacao;
import br.com.bytestorm.insightflow.domain.vo.AnaliseResumo;
import br.com.bytestorm.insightflow.domain.vo.DistribuicaoRisco;
import br.com.bytestorm.insightflow.domain.vo.FiltroAnalise;
import br.com.bytestorm.insightflow.domain.vo.Pagina;
import br.com.bytestorm.insightflow.domain.vo.Paginacao;
import br.com.bytestorm.insightflow.domain.vo.TotaisAnalises;
import br.com.bytestorm.insightflow.infra.persistence.entity.AnaliseReuniaoJpaEntity;
import br.com.bytestorm.insightflow.infra.persistence.mapper.AnaliseReuniaoMapper;
import br.com.bytestorm.insightflow.infra.persistence.query.AnaliseReuniaoConsultas;
import br.com.bytestorm.insightflow.infra.persistence.repository.AnaliseReuniaoJpaRepository;
import br.com.bytestorm.insightflow.infra.persistence.repository.ProdutoTotvsJpaRepository;
import br.com.bytestorm.insightflow.infra.persistence.repository.ReuniaoJpaRepository;

@Repository
@Transactional(readOnly = true)
public class AnaliseReuniaoRepositoryAdapter implements AnaliseReuniaoRepository {

    private final AnaliseReuniaoJpaRepository jpaRepository;
    private final ReuniaoJpaRepository reuniaoJpaRepository;
    private final ProdutoTotvsJpaRepository produtoTotvsJpaRepository;
    private final AnaliseReuniaoConsultas consultas;

    public AnaliseReuniaoRepositoryAdapter(
        AnaliseReuniaoJpaRepository jpaRepository,
        ReuniaoJpaRepository reuniaoJpaRepository,
        ProdutoTotvsJpaRepository produtoTotvsJpaRepository,
        AnaliseReuniaoConsultas consultas
    ) {
        this.jpaRepository = jpaRepository;
        this.reuniaoJpaRepository = reuniaoJpaRepository;
        this.produtoTotvsJpaRepository = produtoTotvsJpaRepository;
        this.consultas = consultas;
    }

    @Override
    @Transactional
    public AnaliseReuniao salvar(AnaliseReuniao analiseReuniao) {
        AnaliseReuniaoJpaEntity entity = AnaliseReuniaoMapper.toJpa(
            analiseReuniao,
            reuniaoJpaRepository.getReferenceById(analiseReuniao.getReuniao().getId()),
            produtoTotvsJpaRepository.getReferenceById(analiseReuniao.getProdutoTotvs().getId())
        );
        return AnaliseReuniaoMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<AnaliseReuniao> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(AnaliseReuniaoMapper::toDomain);
    }

    @Override
    public Optional<AnaliseReuniao> buscarPorReuniaoId(Long reuniaoId) {
        return jpaRepository.findByReuniaoId(reuniaoId).map(AnaliseReuniaoMapper::toDomain);
    }

    @Override
    public Pagina<AnaliseResumo> buscarResumo(FiltroAnalise filtro, Paginacao paginacao) {
        return consultas.buscarResumo(filtro, paginacao);
    }

    @Override
    public TotaisAnalises totais(FiltroAnalise filtro) {
        return consultas.totais(filtro);
    }

    @Override
    public List<DistribuicaoRisco> distribuicaoRiscoPorProduto(FiltroAnalise filtro) {
        return consultas.distribuicaoRiscoPorProduto(filtro);
    }

    @Override
    public List<DistribuicaoRisco> distribuicaoRiscoPorSegmento(FiltroAnalise filtro) {
        return consultas.distribuicaoRiscoPorSegmento(filtro);
    }

    @Override
    public List<AnaliseExportacao> buscarParaExportacao(FiltroAnalise filtro) {
        return consultas.buscarParaExportacao(filtro);
    }
}
