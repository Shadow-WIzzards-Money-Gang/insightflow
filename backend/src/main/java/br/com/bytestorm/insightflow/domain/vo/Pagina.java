package br.com.bytestorm.insightflow.domain.vo;

import java.util.List;
import java.util.function.Function;

/** Uma pagina de resultados e o total de elementos que a consulta inteira tem. */
public record Pagina<T>(List<T> conteudo, long totalElementos) {

    public <R> Pagina<R> mapear(Function<? super T, ? extends R> conversor) {
        return new Pagina<>(conteudo.stream().<R>map(conversor).toList(), totalElementos);
    }
}
