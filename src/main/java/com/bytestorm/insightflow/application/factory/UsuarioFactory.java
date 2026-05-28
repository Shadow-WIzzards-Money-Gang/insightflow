package com.bytestorm.insightflow.application.factory;

import com.bytestorm.insightflow.domain.entity.GerenteVendas;
import com.bytestorm.insightflow.domain.entity.Usuario;
import com.bytestorm.insightflow.domain.entity.Vendedor;
import com.bytestorm.insightflow.domain.enums.TipoUsuario;
import com.bytestorm.insightflow.domain.exceptions.usuario.TipoUsuarioInvalidoException;

public class UsuarioFactory {
    public static Usuario criarUsuario(String nome, String email, String senha, TipoUsuario tipoUsuario) {
        switch (tipoUsuario) {
            case VENDEDOR:
                return new Vendedor(nome, email, senha);
            case GERENTE_VENDAS:
                return new GerenteVendas(nome, email, senha);
            default:
                throw new TipoUsuarioInvalidoException();
        }
    }
}
