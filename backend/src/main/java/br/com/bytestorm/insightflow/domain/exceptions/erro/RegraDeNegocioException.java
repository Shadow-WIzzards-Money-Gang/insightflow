package br.com.bytestorm.insightflow.domain.exceptions.erro;

public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String message) {
        super(message);
    }
}
