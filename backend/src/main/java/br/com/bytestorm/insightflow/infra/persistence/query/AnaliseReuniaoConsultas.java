package br.com.bytestorm.insightflow.infra.persistence.query;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Component;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.vo.AnaliseExportacao;
import br.com.bytestorm.insightflow.domain.vo.AnaliseResumo;
import br.com.bytestorm.insightflow.domain.vo.DistribuicaoRisco;
import br.com.bytestorm.insightflow.domain.vo.FiltroAnalise;
import br.com.bytestorm.insightflow.domain.vo.Pagina;
import br.com.bytestorm.insightflow.domain.vo.Paginacao;
import br.com.bytestorm.insightflow.domain.vo.TotaisAnalises;
import br.com.bytestorm.insightflow.infra.persistence.entity.AnaliseReuniaoJpaEntity;
import br.com.bytestorm.insightflow.infra.persistence.mapper.PaginacaoMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Consultas de analise que fogem dos metodos derivados: projecoes e agregacoes
 * feitas no banco (sem hidratar entidade, sem N+1), projetadas direto nos
 * modelos de leitura do dominio.
 */
@Component
public class AnaliseReuniaoConsultas {

    @PersistenceContext
    private EntityManager em;

    /** Pagina da listagem (1 query + 1 count, este so quando necessario). */
    public Pagina<AnaliseResumo> buscarResumo(FiltroAnalise filtro, Paginacao paginacao) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AnaliseResumo> cq = cb.createQuery(AnaliseResumo.class);
        Root<AnaliseReuniaoJpaEntity> root = cq.from(AnaliseReuniaoJpaEntity.class);

        cq.select(cb.construct(
            AnaliseResumo.class,
            root.get("id"),
            root.get("assunto"),
            root.get("nota"),
            root.get("sentimentoReuniao"),
            root.get("riscoCancelamento"),
            root.get("motivoCancelamento"),
            root.get("reuniao").get("dataReuniao")
        ));

        aplicarFiltro(cq, AnaliseReuniaoFiltro.predicados(root, cb, filtro));

        Sort sort = PaginacaoMapper.toSort(paginacao.ordenacoes());
        if (sort.isSorted()) {
            cq.orderBy(QueryUtils.toOrders(sort, root, cb));
        } else {
            cq.orderBy(cb.asc(root.get("id")));
        }

        var query = em.createQuery(cq);
        query.setFirstResult((int) paginacao.deslocamento());
        query.setMaxResults(paginacao.tamanho());
        List<AnaliseResumo> conteudo = query.getResultList();

        return new Pagina<>(conteudo, total(filtro, paginacao, conteudo.size()));
    }

    // Uma pagina incompleta ja revela o total, e ai o count e dispensado.
    private long total(FiltroAnalise filtro, Paginacao paginacao, int encontrados) {
        boolean ultimaPagina = encontrados < paginacao.tamanho()
            && (encontrados > 0 || paginacao.deslocamento() == 0);
        return ultimaPagina ? paginacao.deslocamento() + encontrados : contar(filtro);
    }

    private long contar(FiltroAnalise filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AnaliseReuniaoJpaEntity> root = cq.from(AnaliseReuniaoJpaEntity.class);
        cq.select(cb.count(root));
        aplicarFiltro(cq, AnaliseReuniaoFiltro.predicados(root, cb, filtro));
        return em.createQuery(cq).getSingleResult();
    }

    /** Agregados globais (COUNT / conditional COUNT / AVG) numa unica linha. */
    public TotaisAnalises totais(FiltroAnalise filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TotaisAnalises> cq = cb.createQuery(TotaisAnalises.class);
        Root<AnaliseReuniaoJpaEntity> root = cq.from(AnaliseReuniaoJpaEntity.class);

        cq.select(cb.construct(
            TotaisAnalises.class,
            cb.count(root),
            contarSe(cb, root.get("riscoCancelamento"), RiscoCancelamento.MUITO_ALTO),
            contarSe(cb, root.get("riscoCancelamento"), RiscoCancelamento.ALTO),
            contarSe(cb, root.get("riscoCancelamento"), RiscoCancelamento.MODERADO),
            contarSe(cb, root.get("riscoCancelamento"), RiscoCancelamento.BAIXO),
            contarSe(cb, root.get("sentimentoReuniao"), SentimentoReuniao.POSITIVO),
            contarSe(cb, root.get("sentimentoReuniao"), SentimentoReuniao.NEUTRO),
            contarSe(cb, root.get("sentimentoReuniao"), SentimentoReuniao.NEGATIVO),
            cb.coalesce(cb.avg(root.get("nota")), cb.literal(0.0))
        ));

        aplicarFiltro(cq, AnaliseReuniaoFiltro.predicados(root, cb, filtro));

        return em.createQuery(cq).getSingleResult();
    }

    /** Distribuicao de risco por produto (GROUP BY nome), ordenada por total desc, nome asc. */
    public List<DistribuicaoRisco> distribuicaoRiscoPorProduto(FiltroAnalise filtro) {
        return distribuicao(filtro, root -> root.get("produtoTotvs").get("nome"));
    }

    /** Distribuicao de risco por segmento (inner join -> ignora analises sem segmento). */
    public List<DistribuicaoRisco> distribuicaoRiscoPorSegmento(FiltroAnalise filtro) {
        return distribuicao(filtro, root -> root.get("reuniao").get("segmentoCliente").get("nome"));
    }

    private List<DistribuicaoRisco> distribuicao(
        FiltroAnalise filtro, Function<Root<AnaliseReuniaoJpaEntity>, Path<String>> chave
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DistribuicaoRisco> cq = cb.createQuery(DistribuicaoRisco.class);
        Root<AnaliseReuniaoJpaEntity> root = cq.from(AnaliseReuniaoJpaEntity.class);
        Path<String> nome = chave.apply(root);
        Expression<Long> total = cb.count(root);
        Path<RiscoCancelamento> risco = root.get("riscoCancelamento");

        cq.select(cb.construct(
            DistribuicaoRisco.class,
            nome,
            total,
            contarSe(cb, risco, RiscoCancelamento.MUITO_ALTO),
            contarSe(cb, risco, RiscoCancelamento.ALTO),
            contarSe(cb, risco, RiscoCancelamento.MODERADO),
            contarSe(cb, risco, RiscoCancelamento.BAIXO)
        ));

        aplicarFiltro(cq, AnaliseReuniaoFiltro.predicados(root, cb, filtro));
        cq.groupBy(nome);
        cq.orderBy(cb.desc(total), cb.asc(nome));

        return em.createQuery(cq).getResultList();
    }

    /** Linhas da exportacao, projetadas direto do banco (1 query). */
    public List<AnaliseExportacao> buscarParaExportacao(FiltroAnalise filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AnaliseExportacao> cq = cb.createQuery(AnaliseExportacao.class);
        Root<AnaliseReuniaoJpaEntity> root = cq.from(AnaliseReuniaoJpaEntity.class);

        Join<Object, Object> produto = root.join("produtoTotvs");
        Join<Object, Object> reuniao = root.join("reuniao");
        Join<Object, Object> segmento = reuniao.join("segmentoCliente", JoinType.LEFT);

        cq.select(cb.construct(
            AnaliseExportacao.class,
            root.get("id"),
            root.get("assunto"),
            root.get("pontosPositivos"),
            root.get("pontosNegativos"),
            root.get("nota"),
            root.get("sentimentoReuniao"),
            root.get("riscoCancelamento"),
            root.get("motivoCancelamento"),
            produto.get("nome"),
            segmento.get("nome"),
            reuniao.get("dataReuniao"),
            reuniao.get("duracao"),
            reuniao.get("hashTranscricao"),
            reuniao.get("transcricaoBruta")
        ));

        List<Predicate> predicados = AnaliseReuniaoFiltro.predicados(
            cb,
            produto.get("nome"),
            segmento.get("nome"),
            root.get("sentimentoReuniao"),
            root.get("riscoCancelamento"),
            filtro
        );
        aplicarFiltro(cq, predicados);
        cq.orderBy(cb.asc(root.get("id")));

        return em.createQuery(cq).getResultList();
    }

    // COUNT(CASE WHEN campo = valor THEN 1 END) -> conta so as linhas que batem.
    private Expression<Long> contarSe(CriteriaBuilder cb, Expression<?> campo, Enum<?> valor) {
        return cb.count(
            cb.selectCase()
                .when(cb.equal(campo, valor), cb.literal(1))
                .otherwise(cb.nullLiteral(Integer.class))
        );
    }

    private void aplicarFiltro(CriteriaQuery<?> cq, List<Predicate> predicados) {
        if (!predicados.isEmpty()) {
            cq.where(predicados.toArray(Predicate[]::new));
        }
    }
}
