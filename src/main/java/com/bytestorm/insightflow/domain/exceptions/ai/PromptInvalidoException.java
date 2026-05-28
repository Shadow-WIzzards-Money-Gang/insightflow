package com.bytestorm.insightflow.domain.exceptions.ai;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class PromptInvalidoException extends BaseException {
    public PromptInvalidoException() {
        super("Prompt inválido.");
    }
}
