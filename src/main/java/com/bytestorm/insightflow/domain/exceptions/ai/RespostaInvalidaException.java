package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class RespostaInvalidaException extends BaseException {
    public RespostaInvalidaException() {
        super("Resposta inválida da API");
    }
}
