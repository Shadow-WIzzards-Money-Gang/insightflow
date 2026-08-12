package br.com.bytestorm.insightflow.application.dto.response;

import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;

public record ProdutoTotvsResponse(

    Long id,
    String nome,
    String categoria

) {

    public static ProdutoTotvsResponse fromEntity(ProdutoTotvs produtoTotvs) {
        return new ProdutoTotvsResponse(produtoTotvs.getId(), produtoTotvs.getNome(), produtoTotvs.getCategoria());
    }

}
