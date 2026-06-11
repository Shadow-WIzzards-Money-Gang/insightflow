package com.bytestorm.insightflow.domain.exceptions.reuniao;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ReuniaoCurtaException extends BaseException {
    public ReuniaoCurtaException() {
        super("Reunião muito curta! A duração mínima é de 20 minutos.");
    }
}
