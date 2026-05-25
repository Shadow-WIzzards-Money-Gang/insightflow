package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.domain.enums.TipoUsuario;

public class Vendedor extends Usuario {
    public Vendedor(String nome, String email, String senha) {
        super(nome, email, senha);
    }

    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.VENDEDOR;
    }
}
