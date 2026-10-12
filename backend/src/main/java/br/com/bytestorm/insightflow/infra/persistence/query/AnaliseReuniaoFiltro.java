package br.com.bytestorm.insightflow.infra.persistence.query;

import java.util.ArrayList;
import java.util.List;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.vo.FiltroAnalise;
import br.com.bytestorm.insightflow.infra.persistence.entity.AnaliseReuniaoJpaEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Traduz o {@link FiltroAnalise} em predicados Criteria (produto, segmento,
 * sentimento, risco).
 *
 * Fonte unica: usado por todas as consultas de {@link AnaliseReuniaoConsultas},
 * para que o mesmo filtro valha na listagem, nas agregacoes e na exportacao.
 *
 * A sobrecarga que recebe {@code Expression}s permite ao chamador apontar para
 * joins que ele mesmo criou (ex.: LEFT JOIN em segmento na exportacao).
 */
public final class AnaliseReuniaoFiltro {

    private AnaliseReuniaoFiltro() {
    }

    public static List<Predicate> predicados(
        Root<AnaliseReuniaoJpaEntity> root, CriteriaBuilder cb, FiltroAnalise filtro
    ) {
        return predicados(
            cb,
            root.get("produtoTotvs").get("nome"),
            root.get("reuniao").get("segmentoCliente").get("nome"),
            root.get("sentimentoReuniao"),
            root.get("riscoCancelamento"),
            filtro
        );
    }

    public static List<Predicate> predicados(
        CriteriaBuilder cb,
        Expression<String> produtoNome,
        Expression<String> segmentoNome,
        Expression<SentimentoReuniao> sentimento,
        Expression<RiscoCancelamento> risco,
        FiltroAnalise filtro
    ) {
        List<Predicate> predicates = new ArrayList<>();

        // Nomes ja chegam em minusculas no FiltroAnalise.
        if (!filtro.produtos().isEmpty()) {
            predicates.add(cb.lower(produtoNome).in(filtro.produtos()));
        }

        if (!filtro.segmentos().isEmpty()) {
            predicates.add(cb.lower(segmentoNome).in(filtro.segmentos()));
        }

        if (!filtro.sentimentos().isEmpty()) {
            predicates.add(sentimento.in(filtro.sentimentos()));
        }

        if (!filtro.riscos().isEmpty()) {
            predicates.add(risco.in(filtro.riscos()));
        }

        return predicates;
    }
}
