package com.bytestorm.insightflow.domain.exceptions;

import com.bytestorm.insightflow.utils.Cor;

public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(Cor.VERMELHO.getCodigo() + message + Cor.RESET.getCodigo());
    }
}
