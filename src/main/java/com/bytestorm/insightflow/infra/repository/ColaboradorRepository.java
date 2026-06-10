package com.bytestorm.insightflow.infra.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bytestorm.insightflow.domain.entity.Colaborador;
import com.bytestorm.insightflow.domain.exceptions.usuario.UsuarioInvalidoException;
import com.bytestorm.insightflow.domain.exceptions.usuarioRepository.EmailJaCadastradoException;
import com.bytestorm.insightflow.domain.interfaces.Repository;

public class ColaboradorRepository implements Repository<Colaborador, UUID> {

    private static ColaboradorRepository instance;

    private List<Colaborador> colaboradores;

    private ColaboradorRepository() {
        this.colaboradores = new ArrayList<>();
    }

    public static ColaboradorRepository getInstance() {
        if (instance == null) {
            instance = new ColaboradorRepository();
        }
        return instance;
    }

    @Override
    public void save(Colaborador colaborador) {

        if (colaborador == null) {
            throw new UsuarioInvalidoException();
        }

        if (this.exists(colaborador.getEmail())) {
            throw new EmailJaCadastradoException();
        }

        this.colaboradores.add(colaborador);
    }

    public Boolean exists(String email) {
        return this.findByEmail(email).isPresent();
    }

    @Override
    public List<Colaborador> findAll() {
        return this.colaboradores;
    }

    @Override
    public Optional<Colaborador> findById(UUID id) {
        return this.colaboradores.stream()
                .filter(colaborador -> colaborador.getId().equals(id))
                .findFirst();
    }

    public Optional<Colaborador> findByEmail(String email) {
        return this.colaboradores.stream()
                .filter(colaborador -> colaborador.getEmail().equals(email))
                .findFirst();
    }
}
