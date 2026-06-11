package com.bytestorm.insightflow.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import com.bytestorm.insightflow.domain.entity.Reuniao;
import com.bytestorm.insightflow.domain.enums.ProdutoTotvs;
import com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import com.bytestorm.insightflow.domain.enums.SegmentoCliente;
import com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import com.bytestorm.insightflow.infra.repository.AnaliseReuniaoRepository;
import com.bytestorm.insightflow.infra.repository.ReuniaoRepository;

public class ReuniaoService {
    private static ReuniaoService instance;

    private ReuniaoRepository reuniaoRepository = ReuniaoRepository.getInstance();
    private AnaliseReuniaoRepository analiseReuniaoRepository = AnaliseReuniaoRepository.getInstance();

    public static ReuniaoService getInstance() {
        if (instance == null) {
            instance = new ReuniaoService();
        }
        return instance;
    }

    public List<Reuniao> getReunioes() {
        return this.reuniaoRepository.findAll();
    }

    public List<AnaliseReuniao> getAnalises() {
        return this.analiseReuniaoRepository.findAll();
    }

    public HashMap<String, String> getMetricas() {
        HashMap<String, String> metricas = new HashMap<>();
        List<AnaliseReuniao> analises = this.analiseReuniaoRepository.findAll();

        if (analises.size() == 0) {
            return metricas;
        }

        int totalReunioes = analises.size();
        double mediaDuracao = this.calcularMediaDuracao();
        SentimentoReuniao sentimentoMedio = this.calcularSentimentoMedio();
        RiscoCancelamento riscoMedio = this.calcularRiscoMedio();
        ProdutoTotvs produto = this.identificarProdutoMaisMencionado();
        SegmentoCliente segmento = this.identificarSegmentoMaisRelevante();

        metricas.put("Total reuniões analisadas", String.valueOf(totalReunioes));
        metricas.put("Média de duração das reuniões", String.valueOf(mediaDuracao));
        metricas.put("Sentimmento médio", sentimentoMedio.getDescricao());
        metricas.put("Risco médio de cancelamento", riscoMedio.getDescricao());
        metricas.put("Produto mais mencionado", produto.getDescricao());
        metricas.put("Segmento mais relevante", segmento.getDescricao());

        return metricas;
    }

    private Double calcularMediaDuracao() {
        List<AnaliseReuniao> analises = this.analiseReuniaoRepository.findAll();

        double mediaDuracao = analises.stream().mapToDouble(analise -> analise.getReuniao().getDuracao().toMinutes()).average().orElse(0);
        return mediaDuracao;
    }

    private SentimentoReuniao calcularSentimentoMedio() {
        List<AnaliseReuniao> analises = this.analiseReuniaoRepository.findAll();

        double mediaSentimento = analises.stream().mapToDouble(analise -> analise.getSentimentoReuniao().getValor()).average().orElse(0);
        return SentimentoReuniao.fromValor(mediaSentimento);
    }

    private RiscoCancelamento calcularRiscoMedio() {
        List<AnaliseReuniao> analises = this.analiseReuniaoRepository.findAll();

        double mediaRisco = analises.stream().mapToDouble(analise -> analise.getRiscoCancelamento().getValor()).average().orElse(0);
        return RiscoCancelamento.fromValor(mediaRisco);
    }

    private ProdutoTotvs identificarProdutoMaisMencionado() {
        List<AnaliseReuniao> analises = this.analiseReuniaoRepository.findAll();

        return analises.stream()
                .collect(Collectors.groupingBy(AnaliseReuniao::getProdutoTotvs, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(ProdutoTotvs.NAO_IDENTIFICADO);
    }

    private SegmentoCliente identificarSegmentoMaisRelevante() {
        List<AnaliseReuniao> analises = this.analiseReuniaoRepository.findAll();

        return analises.stream()
                .collect(Collectors.groupingBy(AnaliseReuniao::getSegmentoCliente, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(SegmentoCliente.NAO_IDENTIFICADO);
    }
}
