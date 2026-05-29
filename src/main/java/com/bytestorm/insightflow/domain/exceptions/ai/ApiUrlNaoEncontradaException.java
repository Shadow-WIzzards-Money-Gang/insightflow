package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ApiUrlNaoEncontradaException extends BaseException {
    public ApiUrlNaoEncontradaException() {
        super("URL da API não encontrada. Verifique o arquivo application.properties");
    }
}
