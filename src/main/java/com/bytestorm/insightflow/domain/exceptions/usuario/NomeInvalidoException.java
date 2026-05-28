package com.bytestorm.insightflow.domain.exceptions.usuario;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class NomeInvalidoException extends BaseException {
    public NomeInvalidoException() {
        super("Nome inválido. Deve conter apenas letras e espaços.");
    }
}
