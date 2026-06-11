package com.bytestorm.insightflow.domain.enums;

public enum RiscoCancelamento {

    MUITO_ALTO("Muito Alto", 1.0),
    ALTO("Alto", 0.75),
    MODERADO("Moderado", 0.5),
    BAIXO("Baixo", 0.25);

    private final String descricao;
    private final Double valor;

    RiscoCancelamento(String descricao, Double valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getValor() {
        return valor;
    }

    public static RiscoCancelamento fromValor(Double valor) {
        for (RiscoCancelamento risco : RiscoCancelamento.values()) {
            if (risco.getValor().equals(valor)) {
                return risco;
            }
        }
        return null;
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
