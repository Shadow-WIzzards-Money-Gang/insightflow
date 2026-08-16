package br.com.bytestorm.insightflow.domain.exceptions.reuniao;

import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro400Exception;

public class ReuniaoJaAnalisadaException extends Erro400Exception {
    public ReuniaoJaAnalisadaException() {
        super("Reunião já analisada.");
    }
}
