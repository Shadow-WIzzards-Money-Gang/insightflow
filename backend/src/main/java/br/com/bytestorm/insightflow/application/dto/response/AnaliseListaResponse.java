package br.com.bytestorm.insightflow.application.dto.response;

import java.time.LocalDateTime;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

/**
 * Projecao enxuta usada na listagem paginada de analises (GET /api/analises).
 * So carrega o que a tela, o cache do front e a exportacao em PDF consomem —
 * sem transcricao, pontos +/-, produto, duracao, etc. (esses ficam no
 * detalhe, GET /api/analises/{id}).
 */
public record AnaliseListaResponse(
    Long id,
    String assunto,
    Integer nota,
    SentimentoReuniao sentimentoReuniao,
    RiscoCancelamento riscoCancelamento,
    String motivoCancelamento,
    ReuniaoResumoResponse reuniao
) {

    public record ReuniaoResumoResponse(LocalDateTime dataReuniao) {
    }
}
