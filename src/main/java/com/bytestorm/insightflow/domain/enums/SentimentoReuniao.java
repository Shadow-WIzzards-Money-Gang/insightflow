package com.bytestorm.insightflow.domain.enums;

public enum SentimentoReuniao {

    POSITIVO("Positivo", 1.0),
    NEUTRO("Neutro", 0.0),
    NEGATIVO("Negativo", -1.0);

    private final String descricao;
    private final Double valor;

    SentimentoReuniao(String descricao, Double valor) {
        this.descricao = descricao;
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public Double getValor() {
        return valor;
    }

    public static SentimentoReuniao fromDescricao(String descricao) {
        for (SentimentoReuniao sentimento : SentimentoReuniao.values()) {
            if (sentimento.getDescricao().replace(" ", "_").equalsIgnoreCase(descricao.replace(" ", "_"))) {
                return sentimento;
            }
        }
        return null;
    }

    public static SentimentoReuniao fromValor(Double valor) {
        for (SentimentoReuniao sentimento : SentimentoReuniao.values()) {
            if (sentimento.getValor().equals(valor)) {
                return sentimento;
            }
        }
        return null;
    }
}
