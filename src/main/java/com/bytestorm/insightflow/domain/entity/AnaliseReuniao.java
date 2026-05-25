package com.bytestorm.insightflow.domain.entity;

import com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import com.bytestorm.insightflow.domain.enums.SegmentoCliente;
import com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import com.bytestorm.insightflow.domain.valueobject.AnaliseReuniaoDTO;

public class AnaliseReuniao {
    private String assunto;
    private SentimentoReuniao sentimentoReuniao;
    private RiscoCancelamento riscoCancelamento;
    private SegmentoCliente segmentoCliente;

    public AnaliseReuniao(AnaliseReuniaoDTO analiseDTO) {
        this.assunto = analiseDTO.assunto();
        this.sentimentoReuniao = SentimentoReuniao.fromDescricao(analiseDTO.sentimentoReuniao());
        this.riscoCancelamento = RiscoCancelamento.fromDescricao(analiseDTO.riscoCancelamento());
        this.segmentoCliente = SegmentoCliente.fromDescricao(analiseDTO.segmentoCliente());
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
}
