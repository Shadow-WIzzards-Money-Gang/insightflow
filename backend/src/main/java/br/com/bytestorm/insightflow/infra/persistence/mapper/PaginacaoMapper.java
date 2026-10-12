package br.com.bytestorm.insightflow.infra.persistence.mapper;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.bytestorm.insightflow.domain.vo.Ordenacao;
import br.com.bytestorm.insightflow.domain.vo.Paginacao;

public final class PaginacaoMapper {

    private PaginacaoMapper() {
    }

    public static Pageable toPageable(Paginacao paginacao) {
        return PageRequest.of(paginacao.pagina(), paginacao.tamanho(), toSort(paginacao.ordenacoes()));
    }

    public static Sort toSort(List<Ordenacao> ordenacoes) {
        return Sort.by(ordenacoes.stream()
            .map(o -> o.ascendente() ? Sort.Order.asc(o.campo()) : Sort.Order.desc(o.campo()))
            .toList());
    }
}
