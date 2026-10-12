package br.com.bytestorm.insightflow.domain.vo;

import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

/** Totais do conjunto de analises: quantidade por risco, por sentimento e nota media. */
public record TotaisAnalises(
    Long totalReunioes,
    Long muitoAlto,
    Long alto,
    Long moderado,
    Long baixo,
    Long sentPositivo,
    Long sentNeutro,
    Long sentNegativo,
    Double notaMedia
) {

    /** Sentimento predominante: media dos sentimentos, ponderada pelo valor de cada um. */
    public SentimentoReuniao sentimentoMedio() {
        long positivos = valorOuZero(sentPositivo);
        long neutros = valorOuZero(sentNeutro);
        long negativos = valorOuZero(sentNegativo);
        long total = positivos + neutros + negativos;

        if (total == 0) {
            return SentimentoReuniao.fromValor(0.0);
        }

        double somaValores = (positivos * SentimentoReuniao.POSITIVO.getValor())
            + (neutros * SentimentoReuniao.NEUTRO.getValor())
            + (negativos * SentimentoReuniao.NEGATIVO.getValor());

        return SentimentoReuniao.fromValor(somaValores / total);
    }

    private static long valorOuZero(Long valor) {
        return valor == null ? 0L : valor;
    }
}
