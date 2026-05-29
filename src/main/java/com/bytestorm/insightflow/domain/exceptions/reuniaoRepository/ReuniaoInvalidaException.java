package com.bytestorm.insightflow.domain.exceptions.reuniaoRepository;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ReuniaoInvalidaException extends BaseException {
    public ReuniaoInvalidaException() {
        super("Reunião inválida.");
    }
}
