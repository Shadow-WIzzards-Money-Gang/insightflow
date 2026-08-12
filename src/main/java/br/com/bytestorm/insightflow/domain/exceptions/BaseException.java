package br.com.bytestorm.insightflow.domain.exceptions;

public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }
}
