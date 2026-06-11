package com.bytestorm.insightflow.domain.enums;

public enum TipoColaborador {
    VENDEDOR("Vendedor"),
    GERENTE_VENDAS("Gerente de Vendas");

    private String descricao;

    TipoColaborador(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
