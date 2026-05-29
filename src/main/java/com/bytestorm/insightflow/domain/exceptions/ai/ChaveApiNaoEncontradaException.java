package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ChaveApiNaoEncontradaException extends BaseException {
    public ChaveApiNaoEncontradaException() {
        super("Chave da API não encontrada. Verifique o arquivo application.properties");
    }
}