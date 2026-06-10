package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.domain.enums.TipoColaborador;
import com.bytestorm.insightflow.utils.Validator;

public abstract class Colaborador extends BaseEntity {
    private String nome;
    private String email;
    private String senha;
    private TipoColaborador tipo;

    public Colaborador(String nome, String email, String senha, TipoColaborador tipo) {
        super();
        
        Validator.validarNome(nome);
        Validator.validarEmail(email);
        Validator.validarSenha(senha);

        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public boolean autenticar(String senha) {
        return this.senha.equals(senha);
    }

    public TipoColaborador getTipo() {
        return tipo;
    }
}
