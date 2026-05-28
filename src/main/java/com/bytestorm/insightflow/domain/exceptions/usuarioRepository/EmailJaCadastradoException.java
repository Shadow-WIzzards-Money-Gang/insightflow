package com.bytestorm.insightflow.domain.exceptions.usuarioRepository;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class EmailJaCadastradoException extends BaseException {
    public EmailJaCadastradoException() {
        super("Email já cadastrado.");
    }
}
