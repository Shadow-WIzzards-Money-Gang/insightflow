package com.bytestorm.insightflow.application.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import com.bytestorm.insightflow.application.factory.AnaliseReuniaoFactory;
import com.bytestorm.insightflow.application.factory.ReuniaoFactory;
import com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import com.bytestorm.insightflow.domain.entity.Colaborador;
import com.bytestorm.insightflow.domain.entity.GerenteVendas;
import com.bytestorm.insightflow.domain.entity.Reuniao;
import com.bytestorm.insightflow.domain.exceptions.analise.ReuniaoCurtaException;
import com.bytestorm.insightflow.domain.exceptions.reuniao.DataInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.reuniao.DuracaoInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.reuniao.TranscricaoInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.reuniaoRepository.ReuniaoJaCadastradaException;
import com.bytestorm.insightflow.infra.repository.AnaliseReuniaoRepository;
import com.bytestorm.insightflow.infra.repository.ColaboradorRepository;
import com.bytestorm.insightflow.infra.repository.ReuniaoRepository;

public class GerenteVendasService {
    private static GerenteVendasService instance;

    private ColaboradorRepository colaboradorRepository = ColaboradorRepository.getInstance();
    private ReuniaoRepository reuniaoRepository = ReuniaoRepository.getInstance();
    private AnaliseReuniaoRepository analiseReuniaoRepository = AnaliseReuniaoRepository.getInstance();

    public static GerenteVendasService getInstance() {
        if (instance == null) {
            instance = new GerenteVendasService();
        }
        return instance;
    }

    public List<Colaborador> getColaboradores() {
        return this.colaboradorRepository.findAll();
    }

    public void transcricaoValida(String transcricao) {
        if (transcricao.isEmpty() || transcricao.isBlank()) {
            throw new TranscricaoInvalidaException();
        }

        if (this.reuniaoRepository.existsByTranscricao(transcricao)) {
            throw new ReuniaoJaCadastradaException();
        }
    }

    public void duracaoValida(Duration duracao) {
        if (duracao.isZero() || duracao.isNegative() || duracao == null) {
            throw new DuracaoInvalidaException();
        }

        if (duracao.toMinutes() < 20) {
            throw new ReuniaoCurtaException();
        }
    }

    public void dataValida(LocalDate data) {
        if (data.isAfter(LocalDate.now())) {
            throw new DataInvalidaException();
        }
    }

    public void cadastrarReuniao(String transcricao, Duration duracao, LocalDate ocorreuEm, GerenteVendas uploadedBy) {
        
        this.transcricaoValida(transcricao);
        this.duracaoValida(duracao);
        this.dataValida(ocorreuEm);

        Reuniao reuniao = ReuniaoFactory.criarReuniao(transcricao, duracao, ocorreuEm, uploadedBy);

        this.cadastrarAnalise(reuniao);
        this.reuniaoRepository.save(reuniao);
    }

    public void cadastrarAnalise(Reuniao reuniao) {
        AnaliseReuniao analise = AnaliseReuniaoFactory.criarAnalise(reuniao);
        this.analiseReuniaoRepository.save(analise);
    }
}
