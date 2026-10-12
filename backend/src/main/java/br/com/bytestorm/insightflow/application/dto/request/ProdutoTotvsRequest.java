package br.com.bytestorm.insightflow.application.dto.request;

import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import jakarta.validation.constraints.NotBlank;

public record ProdutoTotvsRequest(
    
    @NotBlank(message = "O nome do produto é obrigatório.") String nome,
    @NotBlank(message = "A categoria do produto é obrigatória.") String categoria

) {

    public ProdutoTotvs toEntity() {
        return ProdutoTotvs.novo(this.nome, this.categoria);
    }
}
