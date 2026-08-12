package br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente;

import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro404Exception;

public class SegmentoNaoEncontradoException extends Erro404Exception {
    public SegmentoNaoEncontradoException() {
        super("Segmento não encontrado.");
    }
}
