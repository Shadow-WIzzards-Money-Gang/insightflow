package com.bytestorm.insightflow.domain.exceptions.usuario;

public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException() {
        super("Email inválido.");
    }
}
