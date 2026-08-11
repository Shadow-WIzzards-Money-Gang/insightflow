package br.com.bytestorm.insightflow.domain.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reunioes")
public class Reuniao extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transcricao_bruta")
    private String transcricaoBruta;

    @Column(name = "data_reuniao")
    private LocalDateTime dataReuniao;

    @Column(name = "duracao")
    private LocalTime duracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segmento_cliente_id")
    private SegmentoCliente segmentoCliente;

}
