package br.com.bytestorm.insightflow.domain.exceptions.erro;

public class IntegracaoInvalidaException extends RuntimeException {

    public IntegracaoInvalidaException(String message) {
        super(message);
    }

    public IntegracaoInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
