package com.bytestorm.insightflow.infra.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bytestorm.insightflow.domain.entity.Usuario;
import com.bytestorm.insightflow.domain.exceptions.usuario.UsuarioInvalidoException;
import com.bytestorm.insightflow.domain.exceptions.usuarioRepository.EmailJaCadastradoException;
import com.bytestorm.insightflow.domain.interfaces.Repository;

public class UsuarioRepository implements Repository<Usuario, UUID> {

    private static UsuarioRepository instance;

    private List<Usuario> usuarios;

    private UsuarioRepository() {
        this.usuarios = new ArrayList<>();
    }

    public static UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
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
    public Optional<Usuario> findById(UUID id) {
        return this.usuarios.stream()
                .filter(usuario -> usuario.getId().equals(id))
                .findFirst();
    }

    public Optional<Usuario> findByEmail(String email) {
        return this.usuarios.stream()
                .filter(usuario -> usuario.getEmail().equals(email))
                .findFirst();
    }
}
