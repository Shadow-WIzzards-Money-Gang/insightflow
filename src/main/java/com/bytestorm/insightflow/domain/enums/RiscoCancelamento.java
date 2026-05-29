package com.bytestorm.insightflow.domain.enums;

public enum RiscoCancelamento {

    MUITO_ALTO("Muito Alto"),
    ALTO("Alto"),
    MODERADO("Moderado"),
    BAIXO("Baixo");

    private final String descricao;

    RiscoCancelamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static RiscoCancelamento fromDescricao(String descricao) {
        for (RiscoCancelamento risco : RiscoCancelamento.values()) {
            if (risco.getDescricao().replace(" ", "_").equalsIgnoreCase(descricao.replace(" ", "_"))) {
                return risco;
            }
        }
        return null;
    }
}
