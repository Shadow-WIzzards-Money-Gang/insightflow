package com.bytestorm.insightflow.domain.exceptions.ai;

public class RespostaInvalidaException extends RuntimeException {
    public RespostaInvalidaException() {
        super("Resposta inválida da API");
    }
}
