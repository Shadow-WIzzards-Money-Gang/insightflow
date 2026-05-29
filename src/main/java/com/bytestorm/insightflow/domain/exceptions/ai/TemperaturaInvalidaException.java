package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class TemperaturaInvalidaException extends BaseException {
    public TemperaturaInvalidaException() {
        super("Temperatura inválida. Verifique o arquivo application.properties");
    }
}
