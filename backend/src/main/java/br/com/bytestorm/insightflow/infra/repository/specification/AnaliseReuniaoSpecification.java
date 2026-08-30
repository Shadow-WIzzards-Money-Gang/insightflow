package br.com.bytestorm.insightflow.infra.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro400Exception;
import jakarta.persistence.criteria.Predicate;

public final class AnaliseReuniaoSpecification {

    private AnaliseReuniaoSpecification() {
    }

    public static Specification<AnaliseReuniao> comFiltros(AnaliseFiltroRequest filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            List<String> produtos = normalizar(filtro.produtos());
            if (!produtos.isEmpty()) {
                predicates.add(cb.lower(root.get("produtoTotvs").<String>get("nome")).in(produtos));
            }

            List<String> segmentos = normalizar(filtro.segmentos());
            if (!segmentos.isEmpty()) {
                predicates.add(cb.lower(root.get("reuniao").get("segmentoCliente").<String>get("nome")).in(segmentos));
            }

            List<SentimentoReuniao> sentimentos = converter(filtro.sentimentos(), SentimentoReuniao.class);
            if (!sentimentos.isEmpty()) {
                predicates.add(root.get("sentimentoReuniao").in(sentimentos));
            }

            List<RiscoCancelamento> riscos = converter(filtro.riscos(), RiscoCancelamento.class);
            if (!riscos.isEmpty()) {
                predicates.add(root.get("riscoCancelamento").in(riscos));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
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
