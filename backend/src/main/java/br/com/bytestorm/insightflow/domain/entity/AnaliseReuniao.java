package br.com.bytestorm.insightflow.domain.entity;

import java.util.Objects;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.vo.ResultadoAnaliseIA;

public class AnaliseReuniao {

    private final Long id;
    private final String assunto;
    private final String pontosPositivos;
    private final String pontosNegativos;
    private final Integer nota;
    private final Reuniao reuniao;
    private final ProdutoTotvs produtoTotvs;
    private final SentimentoReuniao sentimentoReuniao;
    private final RiscoCancelamento riscoCancelamento;
    private final String motivoCancelamento;

    private AnaliseReuniao(
        Long id, String assunto, String pontosPositivos, String pontosNegativos, Integer nota,
        Reuniao reuniao, ProdutoTotvs produtoTotvs, SentimentoReuniao sentimentoReuniao,
        RiscoCancelamento riscoCancelamento, String motivoCancelamento
    ) {
        this.id = id;
        this.assunto = assunto;
        this.pontosPositivos = pontosPositivos;
        this.pontosNegativos = pontosNegativos;
        this.nota = nota;
        this.reuniao = reuniao;
        this.produtoTotvs = produtoTotvs;
        this.sentimentoReuniao = sentimentoReuniao;
        this.riscoCancelamento = riscoCancelamento;
        this.motivoCancelamento = motivoCancelamento;
    }

    /**
     * Cria a analise de uma reuniao a partir do que a IA respondeu. O produto ja
     * chega resolvido contra o catalogo; aqui o resultado e validado e traduzido
     * para os conceitos do dominio.
     */
    public static AnaliseReuniao aPartirDe(ResultadoAnaliseIA resultado, Reuniao reuniao, ProdutoTotvs produtoTotvs) {
        Objects.requireNonNull(reuniao, "reuniao");
        Objects.requireNonNull(produtoTotvs, "produtoTotvs");
        resultado.validar();

        RiscoCancelamento riscoCancelamento = RiscoCancelamento.fromString(resultado.riscoCancelamento());

        return new AnaliseReuniao(
            null,
            resultado.assunto(),
            resultado.pontosPositivos(),
            resultado.pontosNegativos(),
            resultado.nota(),
            reuniao,
            produtoTotvs,
            SentimentoReuniao.fromString(resultado.sentimentoReuniao()),
            riscoCancelamento,
            motivoRelevante(riscoCancelamento, resultado.motivoCancelamento())
        );
    }

    /** Remonta uma analise ja persistida, sem revalidar as regras de criacao. */
    public static AnaliseReuniao reconstituir(
        Long id, String assunto, String pontosPositivos, String pontosNegativos, Integer nota,
        Reuniao reuniao, ProdutoTotvs produtoTotvs, SentimentoReuniao sentimentoReuniao,
        RiscoCancelamento riscoCancelamento, String motivoCancelamento
    ) {
        return new AnaliseReuniao(
            id, assunto, pontosPositivos, pontosNegativos, nota, reuniao, produtoTotvs,
            sentimentoReuniao, riscoCancelamento, motivoCancelamento
        );
    }

    // Risco BAIXO nao tem motivo de cancelamento, mesmo que a IA tenha sugerido um.
    private static String motivoRelevante(RiscoCancelamento riscoCancelamento, String motivoCancelamento) {
        if (riscoCancelamento == RiscoCancelamento.BAIXO) {
            return null;
        }
        return (motivoCancelamento == null || motivoCancelamento.isBlank()) ? null : motivoCancelamento;
    }

    public Long getId() {
        return id;
    }

    public String getAssunto() {
        return assunto;
    }

    public String getPontosPositivos() {
        return pontosPositivos;
    }

    public String getPontosNegativos() {
        return pontosNegativos;
    }

    public Integer getNota() {
        return nota;
    }

    public Reuniao getReuniao() {
        return reuniao;
    }

    public ProdutoTotvs getProdutoTotvs() {
        return produtoTotvs;
    }

    public SentimentoReuniao getSentimentoReuniao() {
        return sentimentoReuniao;
    }

    public RiscoCancelamento getRiscoCancelamento() {
        return riscoCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }
}
