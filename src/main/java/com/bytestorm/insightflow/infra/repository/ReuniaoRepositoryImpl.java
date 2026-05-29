package com.bytestorm.insightflow.infra.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bytestorm.insightflow.domain.entity.Reuniao;
import com.bytestorm.insightflow.domain.exceptions.reuniaoRepository.ReuniaoInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.reuniaoRepository.ReuniaoJaCadastradaException;
import com.bytestorm.insightflow.domain.interfaces.ReuniaoRepository;

public class ReuniaoRepositoryImpl implements ReuniaoRepository {

    private static ReuniaoRepositoryImpl instance;

    private List<Reuniao> reunioes;

    private ReuniaoRepositoryImpl() {
        this.reunioes = new ArrayList<>();
    }

    public static ReuniaoRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new ReuniaoRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(Reuniao reuniao) {
        if (reuniao == null) {
            throw new ReuniaoInvalidaException();
        }

        if (this.exists(reuniao)) {
            throw new ReuniaoJaCadastradaException();
        }

        this.reunioes.add(reuniao);
    }

    private Boolean exists(Reuniao reuniao) {
        return this.reunioes.stream()
                .filter(r -> r.equals(reuniao))
                .findFirst()
                .isPresent();
    }

    @Override
    public List<Reuniao> findAll() {
        return this.reunioes;
    }

    @Override
    public Optional<Reuniao> findById(UUID id) {
        return this.reunioes.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

}
