package br.com.bytestorm.insightflow.domain.exceptions.ia;

import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro502Exception;

/**
 * A IA respondeu, mas o conteudo nao pode ser aproveitado: JSON malformado,
 * campos obrigatorios ausentes, nota fora da faixa ou produto TOTVS inexistente.
 */
public class RespostaIAInvalidaException extends Erro502Exception {

    public RespostaIAInvalidaException(String message) {
        super(message);
    }

    public RespostaIAInvalidaException(Throwable cause) {
        super("A IA retornou uma resposta que nao pode ser interpretada para esta reuniao.", cause);
    }
}
