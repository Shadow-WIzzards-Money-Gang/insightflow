package com.bytestorm.insightflow.domain.exceptions.usuario;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class TipoUsuarioInvalidoException extends BaseException {
    public TipoUsuarioInvalidoException() {
        super("Tipo de usuário inválido");
    }
}
