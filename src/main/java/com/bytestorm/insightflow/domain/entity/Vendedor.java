package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.domain.enums.TipoColaborador;

public class Vendedor extends Colaborador {
    public Vendedor(String nome, String email, String senha) {
        super(nome, email, senha, TipoColaborador.VENDEDOR);
    }
}
