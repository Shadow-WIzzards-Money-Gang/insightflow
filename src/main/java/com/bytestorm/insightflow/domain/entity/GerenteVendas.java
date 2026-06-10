package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.domain.enums.TipoColaborador;

public class GerenteVendas extends Colaborador {
    public GerenteVendas(String nome, String email, String senha) {
        super(nome, email, senha, TipoColaborador.GERENTE_VENDAS);
    }
}
