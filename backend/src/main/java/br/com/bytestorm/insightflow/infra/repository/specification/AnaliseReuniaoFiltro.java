package br.com.bytestorm.insightflow.infra.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro400Exception;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Predicados de filtro das analises (produto, segmento, sentimento, risco).
 *
 * Fonte unica: usado pela {@link AnaliseReuniaoSpecification} (lista via
 * JpaSpecificationExecutor) e pelas queries agregadas do repositorio custom,
 * para que o mesmo filtro valha em todas as consultas.
 *
 * A sobrecarga que recebe {@code Expression}s permite ao chamador apontar para
 * joins que ele mesmo criou (ex.: LEFT JOIN em segmento no export CSV).
 */
public final class AnaliseReuniaoFiltro {

    private AnaliseReuniaoFiltro() {
    }

    public static List<Predicate> predicados(
        Root<AnaliseReuniao> root, CriteriaBuilder cb, AnaliseFiltroRequest filtro
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
        AnaliseFiltroRequest filtro
    ) {
        List<Predicate> predicates = new ArrayList<>();

        List<String> produtos = normalizar(filtro.produtos());
        if (!produtos.isEmpty()) {
            predicates.add(cb.lower(produtoNome).in(produtos));
        }

        List<String> segmentos = normalizar(filtro.segmentos());
        if (!segmentos.isEmpty()) {
            predicates.add(cb.lower(segmentoNome).in(segmentos));
        }

        List<SentimentoReuniao> sentimentos = converter(filtro.sentimentos(), SentimentoReuniao.class);
        if (!sentimentos.isEmpty()) {
            predicates.add(sentimento.in(sentimentos));
        }

        List<RiscoCancelamento> riscos = converter(filtro.riscos(), RiscoCancelamento.class);
        if (!riscos.isEmpty()) {
            predicates.add(risco.in(riscos));
        }

        return predicates;
    }

    private static List<String> normalizar(List<String> valores) {
        if (valores == null) {
            return List.of();
        }
        return valores.stream()
            .filter(valor -> valor != null && !valor.isBlank())
            .map(valor -> valor.trim().toLowerCase(Locale.ROOT))
            .toList();
    }

    private static <E extends Enum<E>> List<E> converter(List<String> valores, Class<E> tipo) {
        if (valores == null) {
            return List.of();
        }
        return valores.stream()
            .filter(valor -> valor != null && !valor.isBlank())
            .map(valor -> converterValor(valor.trim(), tipo))
            .toList();
    }

    private static <E extends Enum<E>> E converterValor(String valor, Class<E> tipo) {
        try {
            return Enum.valueOf(tipo, valor.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new Erro400Exception("Valor invalido para filtro: '" + valor + "'");
        }
    }
}
