package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.domain.enums.TipoColaborador;

public abstract class Colaborador extends BaseEntity {
    private String nome;
    private String email;
    private Usuario usuario;
    private TipoColaborador tipo;

    public Colaborador(String nome, String email, Usuario usuario, TipoColaborador tipo) {
        super();
        
        this.nome = nome;
        this.email = email;
        this.usuario = usuario;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public TipoColaborador getTipo() {
        return tipo;
    }
}
