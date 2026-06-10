package com.bytestorm.insightflow.application.service;

import com.bytestorm.insightflow.application.factory.ColaboradorFactory;
import com.bytestorm.insightflow.domain.entity.Colaborador;
import com.bytestorm.insightflow.domain.enums.TipoColaborador;
import com.bytestorm.insightflow.domain.exceptions.usuario.SenhaIncorretaException;
import com.bytestorm.insightflow.domain.exceptions.usuarioRepository.EmailJaCadastradoException;
import com.bytestorm.insightflow.domain.exceptions.usuarioRepository.UsuarioNaoEncontradoException;
import com.bytestorm.insightflow.infra.repository.ColaboradorRepository;
import com.bytestorm.insightflow.utils.Validator;

public class AcessoColaboradorService {
    private static AcessoColaboradorService instance;
    private ColaboradorRepository repository = ColaboradorRepository.getInstance();

    public static AcessoColaboradorService getInstance() {
        if (instance == null) {
            instance = new AcessoColaboradorService();
        }
        return instance;
    }

    public void cadastrarColaborador(String nome, String email, String senha, TipoColaborador tipo) {

        this.nomeValido(nome);
        this.emailValido(email);
        this.senhaValida(senha);

        Colaborador colaborador = ColaboradorFactory.criarColaborador(nome, email, senha, tipo);
        this.repository.save(colaborador);
    }

    public void autenticar(Colaborador colaborador, String senha) {
        if (!colaborador.autenticar(senha)) {
            throw new SenhaIncorretaException();
        }
    }

    public Colaborador encontrarColaborador(String email) {

        Validator.validarEmail(email);

        if (!repository.exists(email)) {
            throw new UsuarioNaoEncontradoException();
        }

        return repository.findByEmail(email).get();
    }

    public void nomeValido(String nome) {
        Validator.validarNome(nome);
    }

    public void emailValido(String email) {
        Validator.validarEmail(email);

        if (this.repository.exists(email)) {
            throw new EmailJaCadastradoException();
        }
    }

    public void senhaValida(String senha) {
        Validator.validarSenha(senha);
    }
}
