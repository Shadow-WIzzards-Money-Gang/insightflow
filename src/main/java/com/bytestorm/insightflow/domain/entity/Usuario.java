package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.domain.enums.TipoUsuario;
import com.bytestorm.insightflow.domain.interfaces.Autenticavel;

public abstract class Usuario extends BaseEntity implements Autenticavel {
    private String nome;
    private String email;
    private String senha;

    public Usuario(String nome, String email, String senha) {
        super();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public abstract TipoUsuario getTipoUsuario();

    @Override
    public Boolean autenticar(String senha) {
        return this.senha.equals(senha);
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
