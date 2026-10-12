package br.com.bytestorm.insightflow.domain.vo;

import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

/** Visao completa e achatada de uma analise, usada na exportacao. */
public record AnaliseExportacao(
    Long id,
    String assunto,
    String pontosPositivos,
    String pontosNegativos,
    Integer nota,
    SentimentoReuniao sentimentoReuniao,
    RiscoCancelamento riscoCancelamento,
    String motivoCancelamento,
    String produtoTotvs,
    String segmentoCliente,
    LocalDateTime dataReuniao,
    LocalTime duracao,
    String hashTranscricao,
    String transcricaoBruta
) {
}
