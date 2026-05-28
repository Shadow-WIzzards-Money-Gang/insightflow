package com.bytestorm.insightflow.domain.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import com.bytestorm.insightflow.application.service.AnalisarReuniaoService;
import com.bytestorm.insightflow.domain.valueobject.AnaliseReuniaoDTO;
import com.bytestorm.insightflow.utils.Mapper;

public class Reuniao extends BaseEntity {
    private String transcricao;
    private Duration duracao;
    private LocalDateTime ocorreuEm;
    private GerenteVendas uploadedBy;

    private AnaliseReuniao analise;

    public Reuniao(String transcricao, Duration duracao, LocalDateTime ocorreuEm, GerenteVendas uploadedBy) {
        super();

        this.transcricao = transcricao;
        this.duracao = duracao;
        this.ocorreuEm = ocorreuEm;
        this.uploadedBy = uploadedBy;

        String analise = AnalisarReuniaoService.analisarTranscricao(transcricao, (int) duracao.toMinutes());
        
        AnaliseReuniaoDTO analiseDTO = Mapper.parseAnalise(analise);
        this.analise = analiseDTO.toEntity();
    }

    public String getTranscricao() {
        return transcricao;
    }

    public Duration getDuracao() {
        return duracao;
    }

    public LocalDateTime getOcorreuEm() {
        return ocorreuEm;
    }

    public GerenteVendas getUploadedBy() {
        return uploadedBy;
    }

    public AnaliseReuniao getAnalise() {
        return analise;
    }
}
