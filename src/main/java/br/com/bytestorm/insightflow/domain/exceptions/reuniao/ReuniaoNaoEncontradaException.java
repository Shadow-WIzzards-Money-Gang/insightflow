package br.com.bytestorm.insightflow.domain.exceptions.reuniao;

import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro400Exception;

public class ReuniaoNaoEncontradaException extends Erro400Exception {
    public ReuniaoNaoEncontradaException() {
        super("Reunião não encontrada.");
    }
}
