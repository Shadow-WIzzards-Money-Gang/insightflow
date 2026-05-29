package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.application.dto.AnaliseReuniaoDTO;
import com.bytestorm.insightflow.application.service.AnalisarReuniaoService;
import com.bytestorm.insightflow.domain.enums.ProdutoTotvs;
import com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import com.bytestorm.insightflow.domain.enums.SegmentoCliente;
import com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import com.bytestorm.insightflow.utils.Mapper;

public class AnaliseReuniao {
    private String assunto;
    private SentimentoReuniao sentimentoReuniao;
    private RiscoCancelamento riscoCancelamento;
    private SegmentoCliente segmentoCliente;
    private ProdutoTotvs produtoTotvs;

    private Reuniao reuniao;

    public AnaliseReuniao(Reuniao reuniao) {
        this.reuniao = reuniao;

        String analise = AnalisarReuniaoService.analisarTranscricao(reuniao.getTranscricao(), (int) reuniao.getDuracao().toMinutes());
        AnaliseReuniaoDTO analiseDTO = Mapper.parseAnalise(analise);

        this.assunto = analiseDTO.assunto();
        this.sentimentoReuniao = SentimentoReuniao.fromDescricao(analiseDTO.sentimentoReuniao());
        this.riscoCancelamento = RiscoCancelamento.fromDescricao(analiseDTO.riscoCancelamento());
        this.segmentoCliente = SegmentoCliente.fromDescricao(analiseDTO.segmentoCliente());
        this.produtoTotvs = ProdutoTotvs.fromDescricao(analiseDTO.produtoTotvs());
    }

    public AnaliseReuniao(AnaliseReuniaoDTO analiseReuniaoDTO, Reuniao reuniao) {
        this.assunto = analiseReuniaoDTO.assunto();
        this.sentimentoReuniao = SentimentoReuniao.fromDescricao(analiseReuniaoDTO.sentimentoReuniao());
        this.riscoCancelamento = RiscoCancelamento.fromDescricao(analiseReuniaoDTO.riscoCancelamento());
        this.segmentoCliente = SegmentoCliente.fromDescricao(analiseReuniaoDTO.segmentoCliente());
        this.produtoTotvs = ProdutoTotvs.fromDescricao(analiseReuniaoDTO.produtoTotvs());

        this.reuniao = reuniao;
    }

    public String getAssunto() {
        return assunto;
    }

    public SentimentoReuniao getSentimentoReuniao() {
        return sentimentoReuniao;
    }

    public RiscoCancelamento getRiscoCancelamento() {
        return riscoCancelamento;
    }

    public SegmentoCliente getSegmentoCliente() {
        return segmentoCliente;
    }

    public ProdutoTotvs getProdutoTotvs() {
        return produtoTotvs;
    }

    public Reuniao getReuniao() {
        return reuniao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AnaliseReuniao that = (AnaliseReuniao) o;

        return this.getReuniao().equals(that.getReuniao());
    }
}
