package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ModeloIaNaoEncontradoException extends BaseException {
    public ModeloIaNaoEncontradoException() {
        super("Modelo de IA não encontrado. Verifique o arquivo application.properties");
    }
}
