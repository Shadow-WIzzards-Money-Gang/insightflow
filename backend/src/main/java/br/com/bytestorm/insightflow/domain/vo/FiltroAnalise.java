package br.com.bytestorm.insightflow.domain.vo;

import java.util.List;
import java.util.Locale;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;

/**
 * Filtro das analises (produto, segmento, sentimento, risco). Uma lista vazia
 * significa "sem restricao". Nomes de produto e segmento ficam em minusculas,
 * pois a comparacao ignora caixa.
 */
public record FiltroAnalise(
    List<String> produtos,
    List<String> segmentos,
    List<SentimentoReuniao> sentimentos,
    List<RiscoCancelamento> riscos
) {

    public static FiltroAnalise de(
        List<String> produtos, List<String> segmentos, List<String> sentimentos, List<String> riscos
    ) {
        return new FiltroAnalise(
            normalizar(produtos),
            normalizar(segmentos),
            converter(sentimentos, SentimentoReuniao.class),
            converter(riscos, RiscoCancelamento.class)
        );
    }

    private static List<String> normalizar(List<String> valores) {
        if (valores == null) {
            return List.of();
        }
        return valores.stream()
            .filter(valor -> valor != null && !valor.isBlank())
            .map(valor -> valor.trim().toLowerCase(Locale.ROOT))
            .toList();
    }

    private static <E extends Enum<E>> List<E> converter(List<String> valores, Class<E> tipo) {
        if (valores == null) {
            return List.of();
        }
        return valores.stream()
            .filter(valor -> valor != null && !valor.isBlank())
            .map(valor -> converterValor(valor.trim(), tipo))
            .toList();
    }

    private static <E extends Enum<E>> E converterValor(String valor, Class<E> tipo) {
        try {
            return Enum.valueOf(tipo, valor.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Valor invalido para filtro: '" + valor + "'");
        }
    }
}
