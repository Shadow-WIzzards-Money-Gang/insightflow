package com.bytestorm.insightflow.infra.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import com.bytestorm.insightflow.domain.exceptions.analise.AnaliseInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.analiseReuniaoRepository.AnaliseJaCadastradaException;
import com.bytestorm.insightflow.domain.interfaces.Repository;

public class AnaliseReuniaoRepository implements Repository<AnaliseReuniao, String> {

    private static AnaliseReuniaoRepository instance;

    private List<AnaliseReuniao> analises;

    private AnaliseReuniaoRepository() {
        this.analises = new ArrayList<>();
    }

    public static AnaliseReuniaoRepository getInstance() {
        if (instance == null) {
            instance = new AnaliseReuniaoRepository();
        }
        return instance;
    }

    @Override
    public Optional<AnaliseReuniao> findById(String id) {
        return this.analises.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<AnaliseReuniao> findAll() {
        return this.analises;
    }

    @Override
    public void save(AnaliseReuniao analise) {
        if (analise == null) {
            throw new AnaliseInvalidaException();
        }

        if (this.exists(analise)) {
            throw new AnaliseJaCadastradaException();
        }

        this.analises.add(analise);
    }

    private Boolean exists(AnaliseReuniao analise) {
        return this.analises.stream()
                .filter(a -> a.equals(analise))
                .findFirst()
                .isPresent();
    }

}
