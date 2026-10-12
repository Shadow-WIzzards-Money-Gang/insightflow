package br.com.bytestorm.insightflow.domain.vo;

import java.time.LocalDateTime;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

/** Visao enxuta de uma analise, usada na listagem (sem transcricao, pontos +/-, produto etc.). */
public record AnaliseResumo(
    Long id,
    String assunto,
    Integer nota,
    SentimentoReuniao sentimentoReuniao,
    RiscoCancelamento riscoCancelamento,
    String motivoCancelamento,
    LocalDateTime dataReuniao
) {
}
