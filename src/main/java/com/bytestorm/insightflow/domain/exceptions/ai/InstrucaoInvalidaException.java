package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class InstrucaoInvalidaException extends BaseException {
    public InstrucaoInvalidaException() {
        super("Instrução inválida.");
    }
}
