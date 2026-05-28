package com.bytestorm.insightflow.infra.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bytestorm.insightflow.domain.entity.Usuario;
import com.bytestorm.insightflow.domain.exceptions.usuario.UsuarioInvalidoException;
import com.bytestorm.insightflow.domain.exceptions.usuarioRepository.EmailJaCadastradoException;
import com.bytestorm.insightflow.domain.interfaces.UsuarioRepository;

public class UsuarioRepositoryImpl implements UsuarioRepository {

    private static UsuarioRepositoryImpl instance;

    private List<Usuario> usuarios;

    private UsuarioRepositoryImpl() {
        this.usuarios = new ArrayList<>();
    }

    public static UsuarioRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new UsuarioRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(Usuario usuario) {

        if (usuario == null) {
            throw new UsuarioInvalidoException();
        }

        if (this.exists(usuario.getEmail())) {
            throw new EmailJaCadastradoException();
        }

        this.usuarios.add(usuario);
    }

    public Boolean exists(String email) {
        return this.findByEmail(email).isPresent();
    }

    @Override
    public List<Usuario> findAll() {
        return this.usuarios;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return this.usuarios.stream()
                .filter(usuario -> usuario.getEmail().equals(email))
                .findFirst();
    }
}
