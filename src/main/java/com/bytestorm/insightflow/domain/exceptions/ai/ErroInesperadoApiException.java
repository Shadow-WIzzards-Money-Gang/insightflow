package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ErroInesperadoApiException extends BaseException {
    public ErroInesperadoApiException(Integer statusCode, String responseBody) {
        super("Erro inesperado da Api: " + statusCode + "\n" + responseBody);
    }
}
