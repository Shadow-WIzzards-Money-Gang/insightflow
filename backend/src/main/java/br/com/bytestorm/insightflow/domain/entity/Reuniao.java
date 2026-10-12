package br.com.bytestorm.insightflow.domain.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;
import br.com.bytestorm.insightflow.domain.vo.HashTranscricao;

public class Reuniao {

    private final Long id;
    private final String transcricaoBruta;
    private final HashTranscricao hashTranscricao;
    private final LocalDateTime dataReuniao;
    private final LocalTime duracao;
    private final SegmentoCliente segmentoCliente;
    private final LocalDateTime createdAt;

    private Reuniao(
        Long id, String transcricaoBruta, HashTranscricao hashTranscricao, LocalDateTime dataReuniao,
        LocalTime duracao, SegmentoCliente segmentoCliente, LocalDateTime createdAt
    ) {
        this.id = id;
        this.transcricaoBruta = transcricaoBruta;
        this.hashTranscricao = hashTranscricao;
        this.dataReuniao = dataReuniao;
        this.duracao = duracao;
        this.segmentoCliente = segmentoCliente;
        this.createdAt = createdAt;
    }

    /**
     * Registra uma nova reuniao. O hash da transcricao e o que identifica a
     * reuniao: duas reunioes com a mesma transcricao sao a mesma reuniao.
     */
    public static Reuniao nova(
        String transcricaoBruta, LocalDateTime dataReuniao, LocalTime duracao, SegmentoCliente segmentoCliente
    ) {
        if (transcricaoBruta == null || transcricaoBruta.isBlank()) {
            throw new RegraDeNegocioException("A transcricao nao pode estar vazia!");
        }
        if (segmentoCliente == null) {
            throw new RegraDeNegocioException("O segmento do cliente nao pode estar vazio!");
        }
        return new Reuniao(
            null, transcricaoBruta, HashTranscricao.de(transcricaoBruta), dataReuniao, duracao, segmentoCliente, null
        );
    }

    /** Remonta uma reuniao ja persistida, sem revalidar as regras de criacao. */
    public static Reuniao reconstituir(
        Long id, String transcricaoBruta, HashTranscricao hashTranscricao, LocalDateTime dataReuniao,
        LocalTime duracao, SegmentoCliente segmentoCliente, LocalDateTime createdAt
    ) {
        return new Reuniao(id, transcricaoBruta, hashTranscricao, dataReuniao, duracao, segmentoCliente, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getTranscricaoBruta() {
        return transcricaoBruta;
    }

    public HashTranscricao getHashTranscricao() {
        return hashTranscricao;
    }

    public LocalDateTime getDataReuniao() {
        return dataReuniao;
    }

    public LocalTime getDuracao() {
        return duracao;
    }

    public SegmentoCliente getSegmentoCliente() {
        return segmentoCliente;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
