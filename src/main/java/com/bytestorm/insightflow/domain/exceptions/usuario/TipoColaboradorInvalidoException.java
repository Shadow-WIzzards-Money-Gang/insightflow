package com.bytestorm.insightflow.domain.exceptions.usuario;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class TipoColaboradorInvalidoException extends BaseException {
    public TipoColaboradorInvalidoException() {
        super("Tipo de colaborador inválido.");
    }
}
