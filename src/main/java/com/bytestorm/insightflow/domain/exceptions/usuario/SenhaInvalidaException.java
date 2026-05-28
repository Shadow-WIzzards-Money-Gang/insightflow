package com.bytestorm.insightflow.domain.exceptions.usuario;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class SenhaInvalidaException extends BaseException {
    public SenhaInvalidaException() {
        super("Senha inválida. Deve conter pelo menos 8 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial.");
    }
}
