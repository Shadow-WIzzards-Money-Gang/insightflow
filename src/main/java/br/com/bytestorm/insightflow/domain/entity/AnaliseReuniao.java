package br.com.bytestorm.insightflow.domain.entity;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "analises_reuniao")
public class AnaliseReuniao extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String assunto;

    @Column(name = "pontos_positivos", nullable = false)
    private String pontosPositivos;

    @Column(name = "pontos_negativos", nullable = false)
    private String pontosNegativos;

    @Column(nullable = false)
    private Integer nota;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reuniao_id", nullable = false, unique = true)
    private Reuniao reuniao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_totvs_id", nullable = false)
    private ProdutoTotvs produtoTotvs;

    @Column(name = "sentimento_reuniao", nullable = false)
    @Enumerated(EnumType.STRING)
    private SentimentoReuniao sentimentoReuniao;

    @Column(name = "risco_cancelamento", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiscoCancelamento riscoCancelamento;

}
