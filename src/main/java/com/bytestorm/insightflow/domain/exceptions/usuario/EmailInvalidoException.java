package com.bytestorm.insightflow.domain.exceptions.usuario;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class EmailInvalidoException extends BaseException {
    public EmailInvalidoException() {
        super("Email inválido.");
    }
}
