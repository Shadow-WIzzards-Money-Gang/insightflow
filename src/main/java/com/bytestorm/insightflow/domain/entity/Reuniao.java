package com.bytestorm.insightflow.domain.entity;

import java.time.Duration;
import java.time.LocalDate;

public class Reuniao extends BaseEntity {
    private String transcricao;
    private Duration duracao;
    private LocalDate ocorreuEm;
    private GerenteVendas uploadedBy;

    public Reuniao(String transcricao, Duration duracao, LocalDate ocorreuEm, GerenteVendas uploadedBy) {
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

    public LocalDate getOcorreuEm() {
        return ocorreuEm;
    }

    public GerenteVendas getUploadedBy() {
        return uploadedBy;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Transcrição: %s... | Duração: %s min | Data: %s | Uploaded by: %s", 
        this.getId(),
        this.transcricao.substring(0, 20),
        this.duracao.toMinutes(),
        this.ocorreuEm,
        this.uploadedBy.getNome());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;
        
        Reuniao that = (Reuniao) o;

        return this.getTranscricao().equals(that.getTranscricao());
    }
}
