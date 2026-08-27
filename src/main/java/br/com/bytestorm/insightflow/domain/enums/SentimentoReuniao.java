package br.com.bytestorm.insightflow.domain.enums;

public enum SentimentoReuniao {
    POSITIVO(10.0),
    NEUTRO(5.0),
    NEGATIVO(0.0);

    private final double valor;

    SentimentoReuniao(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return this.valor;
    }

    public static SentimentoReuniao fromString(String s) {
        for (SentimentoReuniao sentim : SentimentoReuniao.values()) {
            if (sentim.name().equalsIgnoreCase(s)) {
                return sentim;
            }
        }
        return NEUTRO;
    }

    public static SentimentoReuniao fromValor(double valor) {
        if (valor >= 8.0) {
            return SentimentoReuniao.POSITIVO;
        } else if (valor >= 3.0) {
            return SentimentoReuniao.NEUTRO;
        } else {
            return SentimentoReuniao.NEGATIVO;
        }
    }
}
