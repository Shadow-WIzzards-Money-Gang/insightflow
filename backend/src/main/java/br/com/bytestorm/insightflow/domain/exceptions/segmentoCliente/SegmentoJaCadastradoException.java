package br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente;

import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;

public class SegmentoJaCadastradoException extends RegraDeNegocioException {
    public SegmentoJaCadastradoException() {
        super("Segmento já cadastrado.");
    }
}
