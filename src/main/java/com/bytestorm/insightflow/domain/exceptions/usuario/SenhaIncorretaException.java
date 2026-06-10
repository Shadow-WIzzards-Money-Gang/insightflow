package com.bytestorm.insightflow.domain.exceptions.usuario;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class SenhaIncorretaException extends BaseException {
    public SenhaIncorretaException() {
        super("Senha incorreta.");
    }
}
