package br.com.bytestorm.insightflow.infra.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import jakarta.persistence.criteria.Predicate;

public final class AnaliseReuniaoSpecification {

    private AnaliseReuniaoSpecification() {
    }

    public static Specification<AnaliseReuniao> comFiltros(AnaliseFiltroRequest filtro) {
        return (root, query, cb) -> cb.and(
            AnaliseReuniaoFiltro.predicados(root, cb, filtro).toArray(Predicate[]::new)
        );
    }
}
