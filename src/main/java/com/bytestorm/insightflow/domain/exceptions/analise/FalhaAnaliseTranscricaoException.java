package com.bytestorm.insightflow.domain.exceptions.analise;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class FalhaAnaliseTranscricaoException extends BaseException {
    public FalhaAnaliseTranscricaoException() {
        super("Falha ao analisar transcrição.");
    }
}
