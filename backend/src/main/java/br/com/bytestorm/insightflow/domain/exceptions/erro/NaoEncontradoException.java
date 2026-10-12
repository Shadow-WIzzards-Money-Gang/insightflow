package br.com.bytestorm.insightflow.domain.exceptions.erro;

public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String message) {
        super(message);
    }
}
