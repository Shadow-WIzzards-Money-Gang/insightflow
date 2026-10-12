package br.com.bytestorm.insightflow.domain.vo;

import br.com.bytestorm.insightflow.domain.exceptions.ia.RespostaIAInvalidaException;

/** O que a IA respondeu sobre uma reuniao, ainda em texto cru. */
public record ResultadoAnaliseIA(
    String assunto,
    String pontosPositivos,
    String pontosNegativos,
    Integer nota,
    String sentimentoReuniao,
    String riscoCancelamento,
    String motivoCancelamento,
    String produtoTotvsNome
) {

    private static final int NOTA_MINIMA = 0;
    private static final int NOTA_MAXIMA = 10;

    public void validar() {
        if (isBlank(assunto)
                || isBlank(sentimentoReuniao)
                || isBlank(riscoCancelamento)
                || isBlank(produtoTotvsNome)
                || nota == null) {
            throw new RespostaIAInvalidaException("A IA retornou uma analise incompleta.");
        }
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new RespostaIAInvalidaException("A IA retornou uma nota fora da faixa de 0 a 10: " + nota);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
