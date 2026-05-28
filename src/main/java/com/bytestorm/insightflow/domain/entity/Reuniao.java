package com.bytestorm.insightflow.domain.entity;

import java.time.Duration;
import java.time.LocalDateTime;

public class Reuniao extends BaseEntity {
    private String transcricao;
    private Duration duracao;
    private LocalDateTime ocorreuEm;
    private GerenteVendas uploadedBy;

    public Reuniao(String transcricao, Duration duracao, LocalDateTime ocorreuEm, GerenteVendas uploadedBy) {
        super();

        this.transcricao = transcricao;
        this.duracao = duracao;
        this.ocorreuEm = ocorreuEm;
        this.uploadedBy = uploadedBy;
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
}
