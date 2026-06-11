package com.bytestorm.insightflow.domain.exceptions.analiseReuniaoRepository;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class AnaliseJaCadastradaException extends BaseException {
    public AnaliseJaCadastradaException() {
        super("Análise já cadastrada!");
    }
}
