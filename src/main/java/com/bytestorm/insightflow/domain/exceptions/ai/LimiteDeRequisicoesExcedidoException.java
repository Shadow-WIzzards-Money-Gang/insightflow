package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class LimiteDeRequisicoesExcedidoException extends BaseException {
    public LimiteDeRequisicoesExcedidoException() {
        super("Limite de requisições excedido.");
    }
}
