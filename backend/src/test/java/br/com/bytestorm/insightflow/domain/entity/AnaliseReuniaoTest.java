package br.com.bytestorm.insightflow.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;
import br.com.bytestorm.insightflow.domain.exceptions.ia.RespostaIAInvalidaException;
import br.com.bytestorm.insightflow.domain.vo.ResultadoAnaliseIA;

class AnaliseReuniaoTest {

    private final SegmentoCliente segmento = SegmentoCliente.reconstituir(1L, "Varejo");
    private final ProdutoTotvs produto = ProdutoTotvs.reconstituir(1L, "Protheus", "ERP");
    private final Reuniao reuniao = Reuniao.nova(
        "cliente reclamou do suporte", LocalDateTime.of(2026, 1, 10, 14, 0), LocalTime.of(0, 45), segmento
    );

    private ResultadoAnaliseIA resultado(Integer nota, String sentimento, String risco, String motivo) {
        return new ResultadoAnaliseIA("Suporte", "agilidade", "demora", nota, sentimento, risco, motivo, "Protheus");
    }

    @Test
    void traduzOResultadoDaIAParaODominio() {
        AnaliseReuniao analise = AnaliseReuniao.aPartirDe(
            resultado(4, "negativo", "muito_alto", "Suporte lento"), reuniao, produto
        );

        assertThat(analise.getId()).isNull();
        assertThat(analise.getNota()).isEqualTo(4);
        assertThat(analise.getSentimentoReuniao()).isEqualTo(SentimentoReuniao.NEGATIVO);
        assertThat(analise.getRiscoCancelamento()).isEqualTo(RiscoCancelamento.MUITO_ALTO);
        assertThat(analise.getMotivoCancelamento()).isEqualTo("Suporte lento");
        assertThat(analise.getProdutoTotvs()).isSameAs(produto);
        assertThat(analise.getReuniao()).isSameAs(reuniao);
    }

    @Test
    void riscoBaixoNaoTemMotivoDeCancelamento() {
        AnaliseReuniao analise = AnaliseReuniao.aPartirDe(
            resultado(9, "POSITIVO", "BAIXO", "A IA inventou um motivo"), reuniao, produto
        );

        assertThat(analise.getMotivoCancelamento()).isNull();
    }

    @Test
    void motivoEmBrancoViraNulo() {
        AnaliseReuniao analise = AnaliseReuniao.aPartirDe(resultado(5, "NEUTRO", "ALTO", "   "), reuniao, produto);

        assertThat(analise.getMotivoCancelamento()).isNull();
    }

    @Test
    void sentimentoERiscoDesconhecidosCaemNoPadrao() {
        AnaliseReuniao analise = AnaliseReuniao.aPartirDe(resultado(5, "eufórico", "catastrófico", null), reuniao, produto);

        assertThat(analise.getSentimentoReuniao()).isEqualTo(SentimentoReuniao.NEUTRO);
        assertThat(analise.getRiscoCancelamento()).isEqualTo(RiscoCancelamento.BAIXO);
    }

    @Test
    void rejeitaNotaForaDaFaixa() {
        assertThatThrownBy(() -> AnaliseReuniao.aPartirDe(resultado(11, "NEUTRO", "ALTO", null), reuniao, produto))
            .isInstanceOf(RespostaIAInvalidaException.class)
            .hasMessageContaining("fora da faixa");

        assertThatThrownBy(() -> AnaliseReuniao.aPartirDe(resultado(-1, "NEUTRO", "ALTO", null), reuniao, produto))
            .isInstanceOf(RespostaIAInvalidaException.class);
    }

    @Test
    void rejeitaAnaliseIncompleta() {
        assertThatThrownBy(() -> AnaliseReuniao.aPartirDe(resultado(null, "NEUTRO", "ALTO", null), reuniao, produto))
            .isInstanceOf(RespostaIAInvalidaException.class)
            .hasMessageContaining("incompleta");

        assertThatThrownBy(() -> AnaliseReuniao.aPartirDe(resultado(5, " ", "ALTO", null), reuniao, produto))
            .isInstanceOf(RespostaIAInvalidaException.class);
    }

    @Test
    void reuniaoExigeTranscricaoECalculaOHash() {
        assertThat(reuniao.getHashTranscricao().valor()).hasSize(32);

        assertThatThrownBy(() -> Reuniao.nova("  ", LocalDateTime.now(), LocalTime.NOON, segmento))
            .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void produtoESegmentoExigemNome() {
        assertThatThrownBy(() -> ProdutoTotvs.novo(" ", "ERP")).isInstanceOf(RegraDeNegocioException.class);
        assertThatThrownBy(() -> ProdutoTotvs.novo("Protheus", null)).isInstanceOf(RegraDeNegocioException.class);
        assertThatThrownBy(() -> SegmentoCliente.novo("")).isInstanceOf(RegraDeNegocioException.class);
    }
}
