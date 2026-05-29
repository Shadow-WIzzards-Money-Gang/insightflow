package com.bytestorm.insightflow.domain.exceptions.reuniaoRepository;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ReuniaoJaCadastradaException extends BaseException {
    public ReuniaoJaCadastradaException() {
        super("Reunião já cadastrada.");
    }
}
