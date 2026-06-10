package com.bytestorm.insightflow.domain.exceptions.usuarioRepository;

import com.bytestorm.insightflow.domain.exceptions.BaseException;

public class UsuarioNaoEncontradoException extends BaseException {
    public UsuarioNaoEncontradoException() {
        super("Usuário não encontrado.");
    }
}
