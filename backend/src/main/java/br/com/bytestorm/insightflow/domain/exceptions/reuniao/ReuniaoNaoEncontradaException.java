package br.com.bytestorm.insightflow.domain.exceptions.reuniao;

import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;

public class ReuniaoNaoEncontradaException extends RegraDeNegocioException {
    public ReuniaoNaoEncontradaException() {
        super("Reunião não encontrada.");
    }
}
