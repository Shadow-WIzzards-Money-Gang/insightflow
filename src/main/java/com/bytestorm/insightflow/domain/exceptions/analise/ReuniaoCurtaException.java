package com.bytestorm.insightflow.domain.exceptions.analise;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ReuniaoCurtaException extends BaseException {
    public ReuniaoCurtaException() {
        super("A reunião é muito curta para análise.");
    }
}
