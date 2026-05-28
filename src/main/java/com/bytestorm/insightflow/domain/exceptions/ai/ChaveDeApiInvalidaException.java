package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ChaveDeApiInvalidaException extends BaseException {
    public ChaveDeApiInvalidaException() {
        super("Chave de API inválida ou não autorizada.");
    }
}
