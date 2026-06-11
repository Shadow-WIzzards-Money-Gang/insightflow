package com.bytestorm.insightflow.domain.exceptions.reuniao;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class TranscricaoInvalidaException extends BaseException {
    public TranscricaoInvalidaException() {
        super("Transcrição inválida!");
    }
}
