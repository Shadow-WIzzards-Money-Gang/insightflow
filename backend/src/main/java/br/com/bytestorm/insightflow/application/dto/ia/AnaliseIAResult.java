package br.com.bytestorm.insightflow.application.dto.ia;

public record AnaliseIAResult(
    String assunto,
    String pontosPositivos,
    String pontosNegativos,
    Integer nota,
    String sentimentoReuniao,
    String riscoCancelamento,
    String motivoCancelamento,
    String produtoTotvsNome
) {

}
