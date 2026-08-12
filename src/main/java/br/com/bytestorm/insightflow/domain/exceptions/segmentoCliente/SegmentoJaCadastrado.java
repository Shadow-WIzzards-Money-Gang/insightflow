package br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente;

import br.com.bytestorm.insightflow.domain.exceptions.BaseException;

public class SegmentoJaCadastrado extends BaseException {
    public SegmentoJaCadastrado() {
        super("Segmento já cadastrado.");
    }
}
