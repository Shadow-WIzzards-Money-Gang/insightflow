package com.bytestorm.insightflow.domain.exceptions.usuario;

public class NomeInvalidoException extends RuntimeException {
    public NomeInvalidoException() {
        super("Nome inválido. Deve conter apenas letras e espaços.");
    }
}
