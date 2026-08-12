package br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente;

import br.com.bytestorm.insightflow.domain.exceptions.BaseException;

public class SegmentoNaoEncontrado extends BaseException {
    public SegmentoNaoEncontrado() {
        super("Segmento não encontrado.");
    }
}
