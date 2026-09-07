package br.com.bytestorm.insightflow.domain.exceptions.erro;

public class Erro503Exception extends RuntimeException {

    public Erro503Exception(String message) {
        super(message);
    }

    public Erro503Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
