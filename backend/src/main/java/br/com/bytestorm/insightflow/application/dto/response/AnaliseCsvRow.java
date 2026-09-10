package br.com.bytestorm.insightflow.application.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;

/**
 * Uma linha da exportacao CSV das analises, montada direto no banco via projecao
 * (sem hidratar entidades / sem N+1). Ordem dos campos = ordem das colunas do CSV.
 */
public record AnaliseCsvRow(
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
