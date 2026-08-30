package br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente;

import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro400Exception;

public class SegmentoJaCadastradoException extends Erro400Exception {
    public SegmentoJaCadastradoException() {
        super("Segmento já cadastrado.");
    }
}
