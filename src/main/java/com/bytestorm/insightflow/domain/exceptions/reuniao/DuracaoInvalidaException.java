package com.bytestorm.insightflow.domain.exceptions.reuniao;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class DuracaoInvalidaException extends BaseException {
    public DuracaoInvalidaException() {
        super("Duração inválida!");
    }
}
