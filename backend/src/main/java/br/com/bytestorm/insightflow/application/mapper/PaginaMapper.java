package br.com.bytestorm.insightflow.application.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.bytestorm.insightflow.domain.vo.Ordenacao;
import br.com.bytestorm.insightflow.domain.vo.Pagina;
import br.com.bytestorm.insightflow.domain.vo.Paginacao;

/** Converte a paginacao da API (Spring Data) de/para a paginacao do dominio. */
public final class PaginaMapper {

    private PaginaMapper() {
    }

    public static Paginacao toPaginacao(Pageable pageable) {
        return new Paginacao(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort().stream()
                .map(order -> new Ordenacao(order.getProperty(), order.isAscending()))
                .toList()
        );
    }

    public static <T> Page<T> toPage(Pagina<T> pagina, Pageable pageable) {
        return new PageImpl<>(pagina.conteudo(), pageable, pagina.totalElementos());
    }
}
