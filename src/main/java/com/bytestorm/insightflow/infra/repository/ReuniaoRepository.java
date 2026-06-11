package com.bytestorm.insightflow.infra.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bytestorm.insightflow.domain.entity.Reuniao;
import com.bytestorm.insightflow.domain.exceptions.reuniaoRepository.ReuniaoInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.reuniaoRepository.ReuniaoJaCadastradaException;
import com.bytestorm.insightflow.domain.interfaces.Repository;

public class ReuniaoRepository implements Repository<Reuniao, String> {

    private static ReuniaoRepository instance;

    private List<Reuniao> reunioes;

    private ReuniaoRepository() {
        this.reunioes = new ArrayList<>();
    }

    public static ReuniaoRepository getInstance() {
        if (instance == null) {
            instance = new ReuniaoRepository();
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

    public Boolean existsByTranscricao(String transcricao) {
        return this.reunioes.stream()
                .filter(r -> r.getTranscricao().equals(transcricao))
                .findFirst()
                .isPresent();
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
    public Optional<Reuniao> findById(String id) {
        return this.reunioes.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }
}
