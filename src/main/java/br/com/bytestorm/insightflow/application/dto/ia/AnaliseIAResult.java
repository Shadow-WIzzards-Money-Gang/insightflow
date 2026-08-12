package br.com.bytestorm.insightflow.application.dto.ia;

public record AnaliseIAResult(
    String assunto,
    String sentimentoReuniao,
    String riscoCancelamento,
    String produtoTotvsNome
) {

}
