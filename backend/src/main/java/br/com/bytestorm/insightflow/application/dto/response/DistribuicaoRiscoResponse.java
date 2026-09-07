package br.com.bytestorm.insightflow.application.dto.response;

public record DistribuicaoRiscoResponse(
    String rotulo,
    Long total,
    Long muitoAlto,
    Long alto,
    Long moderado,
    Long baixo
) {

    // Pesos por severidade: um risco de nivel mais alto sempre "vale" mais do que
    // varios de nivel abaixo, mas o volume ainda soma (ex.: 1 ALTO > 3 MODERADO).
    private static final long PESO_MODERADO = 1;
    private static final long PESO_ALTO = 4;
    private static final long PESO_MUITO_ALTO = 16;

    /** Quantidade de reunioes com risco relevante (MODERADO + ALTO + MUITO_ALTO). */
    public long totalCritico() {
        return muitoAlto + alto + moderado;
    }

    /** Criticidade ponderada pela severidade - usada para eleger produto/segmento mais critico. */
    public long scoreCriticidade() {
        return (muitoAlto * PESO_MUITO_ALTO) + (alto * PESO_ALTO) + (moderado * PESO_MODERADO);
    }
}
