package com.bytestorm.insightflow.utils;

import com.bytestorm.insightflow.domain.exceptions.usuario.EmailInvalidoException;
import com.bytestorm.insightflow.domain.exceptions.usuario.NomeInvalidoException;
import com.bytestorm.insightflow.domain.exceptions.usuario.SenhaInvalidaException;

public class Validator {

    private static final String NOME_REGEX = "^[A-Za-zÀ-ÿ]+(?:\\s[A-Za-zÀ-ÿ]+)*$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String SENHA_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new NomeInvalidoException();
        }

        if (!nome.matches(NOME_REGEX)) {
            throw new NomeInvalidoException();
        }
    }

    public static void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new EmailInvalidoException();
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new EmailInvalidoException();
        }
    }

    public static void validarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new SenhaInvalidaException();
        }

        if (!senha.matches(SENHA_REGEX)) {
            throw new SenhaInvalidaException();
        }
    }
}
