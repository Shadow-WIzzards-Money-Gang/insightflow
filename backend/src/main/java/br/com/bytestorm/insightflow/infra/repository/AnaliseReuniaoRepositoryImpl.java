package br.com.bytestorm.insightflow.infra.repository;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.support.PageableExecutionUtils;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseCsvRow;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseListaResponse;
import br.com.bytestorm.insightflow.application.dto.response.DistribuicaoRiscoResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasGlobais;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.infra.repository.specification.AnaliseReuniaoFiltro;
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

public class AnaliseReuniaoRepositoryImpl implements AnaliseReuniaoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<AnaliseListaResponse> buscarResumo(AnaliseFiltroRequest filtro, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AnaliseListaResponse> cq = cb.createQuery(AnaliseListaResponse.class);
        Root<AnaliseReuniao> root = cq.from(AnaliseReuniao.class);

        cq.select(cb.construct(
            AnaliseListaResponse.class,
            root.get("id"),
            root.get("assunto"),
            root.get("nota"),
            root.get("sentimentoReuniao"),
            root.get("riscoCancelamento"),
            root.get("motivoCancelamento"),
            cb.construct(
                AnaliseListaResponse.ReuniaoResumoResponse.class,
                root.get("reuniao").get("dataReuniao")
            )
        ));

        aplicarFiltro(cq, AnaliseReuniaoFiltro.predicados(root, cb, filtro));

        if (pageable.getSort().isSorted()) {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        } else {
            cq.orderBy(cb.asc(root.get("id")));
        }

        var query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<AnaliseListaResponse> conteudo = query.getResultList();

        return PageableExecutionUtils.getPage(conteudo, pageable, () -> contar(filtro));
    }

    private long contar(AnaliseFiltroRequest filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AnaliseReuniao> root = cq.from(AnaliseReuniao.class);
        cq.select(cb.count(root));
        aplicarFiltro(cq, AnaliseReuniaoFiltro.predicados(root, cb, filtro));
        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public MetricasGlobais metricasGlobais(AnaliseFiltroRequest filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MetricasGlobais> cq = cb.createQuery(MetricasGlobais.class);
        Root<AnaliseReuniao> root = cq.from(AnaliseReuniao.class);

        cq.select(cb.construct(
            MetricasGlobais.class,
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

    @Override
    public List<DistribuicaoRiscoResponse> distribuicaoRiscoPorProduto(AnaliseFiltroRequest filtro) {
        return distribuicao(filtro, root -> root.get("produtoTotvs").get("nome"));
    }

    @Override
    public List<DistribuicaoRiscoResponse> distribuicaoRiscoPorSegmento(AnaliseFiltroRequest filtro) {
        return distribuicao(filtro, root -> root.get("reuniao").get("segmentoCliente").get("nome"));
    }

    private List<DistribuicaoRiscoResponse> distribuicao(
        AnaliseFiltroRequest filtro, Function<Root<AnaliseReuniao>, Path<String>> chave
    ) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DistribuicaoRiscoResponse> cq = cb.createQuery(DistribuicaoRiscoResponse.class);
        Root<AnaliseReuniao> root = cq.from(AnaliseReuniao.class);
        Path<String> nome = chave.apply(root);
        Expression<Long> total = cb.count(root);
        Path<RiscoCancelamento> risco = root.get("riscoCancelamento");

        cq.select(cb.construct(
            DistribuicaoRiscoResponse.class,
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

    @Override
    public List<AnaliseCsvRow> buscarLinhasCsv(AnaliseFiltroRequest filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AnaliseCsvRow> cq = cb.createQuery(AnaliseCsvRow.class);
        Root<AnaliseReuniao> root = cq.from(AnaliseReuniao.class);

        Join<Object, Object> produto = root.join("produtoTotvs");
        Join<Object, Object> reuniao = root.join("reuniao");
        Join<Object, Object> segmento = reuniao.join("segmentoCliente", JoinType.LEFT);

        cq.select(cb.construct(
            AnaliseCsvRow.class,
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
