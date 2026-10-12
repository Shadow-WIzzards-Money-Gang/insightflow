package br.com.bytestorm.insightflow.domain.exceptions.ia;

import br.com.bytestorm.insightflow.domain.exceptions.erro.IntegracaoInvalidaException;

/**
 * A IA respondeu, mas o conteudo nao pode ser aproveitado: JSON malformado,
 * campos obrigatorios ausentes, nota fora da faixa ou produto TOTVS inexistente.
 */
public class RespostaIAInvalidaException extends IntegracaoInvalidaException {

    public RespostaIAInvalidaException(String message) {
        super(message);
    }

    public RespostaIAInvalidaException(Throwable cause) {
        super("A IA retornou uma resposta que nao pode ser interpretada para esta reuniao.", cause);
    }
}
