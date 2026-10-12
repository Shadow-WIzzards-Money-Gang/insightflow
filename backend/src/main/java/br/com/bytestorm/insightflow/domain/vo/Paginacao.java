package br.com.bytestorm.insightflow.domain.vo;

import java.util.List;

/** Pedido de uma pagina: numero (a partir de 0), tamanho e ordenacao. */
public record Paginacao(int pagina, int tamanho, List<Ordenacao> ordenacoes) {

    public long deslocamento() {
        return (long) pagina * tamanho;
    }
}
