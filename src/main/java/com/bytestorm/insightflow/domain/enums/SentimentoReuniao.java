package com.bytestorm.insightflow.domain.enums;

public enum SentimentoReuniao {

    POSITIVO("Positivo"),
    NEUTRO("Neutro"),
    NEGATIVO("Negativo");

    private final String descricao;

    SentimentoReuniao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static SentimentoReuniao fromDescricao(String descricao) {
        for (SentimentoReuniao sentimento : SentimentoReuniao.values()) {
            if (sentimento.getDescricao().equalsIgnoreCase(descricao)) {
                return sentimento;
            }
        }
        return null;
    }
}
