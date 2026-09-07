package br.com.bytestorm.insightflow.domain.exceptions.erro;

public class Erro502Exception extends RuntimeException {

    public Erro502Exception(String message) {
        super(message);
    }

    public Erro502Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
