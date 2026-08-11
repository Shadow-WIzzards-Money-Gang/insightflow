package br.com.bytestorm.insightflow.application.dto;

import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import jakarta.validation.constraints.NotBlank;

public record ProdutoTotvsDTO(
        @NotBlank(message = "O nome do produto é obrigatório.") String nome,
        @NotBlank(message = "A categoria do produto é obrigatória.") String categoria) {

    public static ProdutoTotvsDTO fromEntity(ProdutoTotvs produtoTotvs) {
        return new ProdutoTotvsDTO(produtoTotvs.getNome(), produtoTotvs.getCategoria());
    }

    public ProdutoTotvs toEntity() {
        return ProdutoTotvs.builder()
                .nome(this.nome)
                .categoria(this.categoria)
                .build();
    }
}
