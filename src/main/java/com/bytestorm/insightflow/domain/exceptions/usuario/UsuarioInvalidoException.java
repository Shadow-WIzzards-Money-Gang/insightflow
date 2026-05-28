package com.bytestorm.insightflow.domain.exceptions.usuario;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class UsuarioInvalidoException extends BaseException {
    public UsuarioInvalidoException() {
        super("Usuário inválido");
    }
}
