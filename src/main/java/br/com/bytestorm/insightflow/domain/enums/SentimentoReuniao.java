package br.com.bytestorm.insightflow.domain.enums;

public enum SentimentoReuniao {
    POSITIVO,
    NEUTRO,
    NEGATIVO;

    public static SentimentoReuniao fromString(String s) {
        for (SentimentoReuniao sentim : SentimentoReuniao.values()) {
            if (sentim.name().equalsIgnoreCase(s)) {
                return sentim;
            }
        }
        return NEUTRO;
    }
}
