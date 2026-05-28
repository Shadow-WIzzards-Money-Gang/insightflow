package com.bytestorm.insightflow.domain.exceptions.usuario;

public class UsuarioInvalidoException extends RuntimeException {
    public UsuarioInvalidoException() {
        super("Usuário inválido");
    }
}
