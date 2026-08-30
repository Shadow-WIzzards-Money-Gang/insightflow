package br.com.bytestorm.insightflow.domain.exceptions.erro;

public class Erro400Exception extends RuntimeException {
    public Erro400Exception(String message) {
        super(message);
    }
}
