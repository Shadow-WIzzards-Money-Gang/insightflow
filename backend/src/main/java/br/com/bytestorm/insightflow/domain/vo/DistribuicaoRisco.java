package br.com.bytestorm.insightflow.domain.vo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Quantas analises um produto ou segmento (o rotulo) tem em cada nivel de risco. */
public record DistribuicaoRisco(
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

    /** Elege a distribuicao mais critica; vazio se nenhuma tem risco relevante. */
    public static Optional<DistribuicaoRisco> maisCritica(List<DistribuicaoRisco> distribuicoes) {
        return distribuicoes.stream()
            .filter(d -> d.totalCritico() > 0)
            .max(Comparator.comparingLong(DistribuicaoRisco::scoreCriticidade)
                // desempate: quem tem o risco mais severo em maior quantidade
                .thenComparingLong(DistribuicaoRisco::muitoAlto)
                .thenComparingLong(DistribuicaoRisco::alto)
                .thenComparingLong(DistribuicaoRisco::moderado)
                .thenComparingLong(DistribuicaoRisco::total)
                .thenComparing(Comparator.comparing(DistribuicaoRisco::rotulo).reversed()));
    }
}
