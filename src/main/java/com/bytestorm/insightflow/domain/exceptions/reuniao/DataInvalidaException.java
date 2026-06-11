package com.bytestorm.insightflow.domain.exceptions.reuniao;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class DataInvalidaException extends BaseException {
    public DataInvalidaException() {
        super("Data inválida!");
    }
}
