package com.bytestorm.insightflow.domain.exceptions.menu;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class OpcaoInvalidaException extends BaseException {
    public OpcaoInvalidaException() {
        super("Opção inválida!");
    }
}
