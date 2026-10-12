package br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente;

import br.com.bytestorm.insightflow.domain.exceptions.erro.NaoEncontradoException;

public class SegmentoNaoEncontradoException extends NaoEncontradoException {
    public SegmentoNaoEncontradoException() {
        super("Segmento não encontrado.");
    }
}
