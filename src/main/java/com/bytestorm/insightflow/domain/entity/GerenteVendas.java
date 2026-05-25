package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.domain.enums.TipoUsuario;

public class GerenteVendas extends Usuario {
    public GerenteVendas(String nome, String email, String senha) {
        super(nome, email, senha);
    }

    @Override
    public TipoUsuario getTipoUsuario() {
        return TipoUsuario.GERENTE_VENDAS;
    }
}
