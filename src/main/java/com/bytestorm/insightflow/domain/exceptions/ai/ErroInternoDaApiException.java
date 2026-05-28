package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ErroInternoDaApiException extends BaseException {
    public ErroInternoDaApiException() {
        super("Erro interno da Api.");
    }
}
