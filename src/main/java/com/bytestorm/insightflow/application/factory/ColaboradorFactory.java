package com.bytestorm.insightflow.application.factory;

import com.bytestorm.insightflow.domain.entity.Colaborador;
import com.bytestorm.insightflow.domain.entity.GerenteVendas;
import com.bytestorm.insightflow.domain.entity.Vendedor;
import com.bytestorm.insightflow.domain.enums.TipoColaborador;

public class ColaboradorFactory {
    public static Colaborador criarColaborador(String nome, String email, String senha, TipoColaborador tipo) {
        switch (tipo) {
            case VENDEDOR:
                return new Vendedor(nome, email, senha);
            case GERENTE_VENDAS:
                return new GerenteVendas(nome, email, senha);
            default:
                throw new IllegalArgumentException("Tipo de colaborador inválido");
        }
    }
}
