package br.com.bytestorm.insightflow.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;

class ValueObjectsTest {

    @Test
    void hashEMd5EmHexadecimal() {
        assertThat(HashTranscricao.de("abc").valor()).isEqualTo("900150983cd24fb0d6963f7d28e17f72");
    }

    // Reunioes ja gravadas usam o hash do DigestUtils do Spring; o do dominio precisa bater com ele.
    @Test
    void hashEIgualAoQueJaEstaGravadoNoBanco() {
        String transcricao = "Reunião com acentuação, emoji 🚀 e\nquebra de linha";

        assertThat(HashTranscricao.de(transcricao).valor())
            .isEqualTo(DigestUtils.md5DigestAsHex(transcricao.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void filtroNormalizaNomesEConverteEnums() {
        FiltroAnalise filtro = FiltroAnalise.de(
            List.of(" Protheus ", ""), null, List.of("positivo"), List.of("muito_alto", " ")
        );

        assertThat(filtro.produtos()).containsExactly("protheus");
        assertThat(filtro.segmentos()).isEmpty();
        assertThat(filtro.sentimentos()).containsExactly(SentimentoReuniao.POSITIVO);
        assertThat(filtro.riscos()).containsExactly(RiscoCancelamento.MUITO_ALTO);
    }

    @Test
    void filtroRejeitaValorInvalido() {
        assertThatThrownBy(() -> FiltroAnalise.de(null, null, null, List.of("gigante")))
            .isInstanceOf(RegraDeNegocioException.class)
            .hasMessage("Valor invalido para filtro: 'gigante'");
    }

    @Test
    void umRiscoAltoPesaMaisQueTresModerados() {
        DistribuicaoRisco tresModerados = new DistribuicaoRisco("A", 3L, 0L, 0L, 3L, 0L);
        DistribuicaoRisco umAlto = new DistribuicaoRisco("B", 1L, 0L, 1L, 0L, 0L);

        assertThat(DistribuicaoRisco.maisCritica(List.of(tresModerados, umAlto))).contains(umAlto);
    }

    @Test
    void empateDeScoreEDecididoPeloRiscoMaisSevero() {
        DistribuicaoRisco quatroAltos = new DistribuicaoRisco("A", 4L, 0L, 4L, 0L, 0L);
        DistribuicaoRisco umMuitoAlto = new DistribuicaoRisco("B", 1L, 1L, 0L, 0L, 0L);

        assertThat(quatroAltos.scoreCriticidade()).isEqualTo(umMuitoAlto.scoreCriticidade());
        assertThat(DistribuicaoRisco.maisCritica(List.of(quatroAltos, umMuitoAlto))).contains(umMuitoAlto);
    }

    @Test
    void empateTotalEDecididoPelaOrdemAlfabetica() {
        DistribuicaoRisco b = new DistribuicaoRisco("B", 1L, 0L, 1L, 0L, 0L);
        DistribuicaoRisco a = new DistribuicaoRisco("A", 1L, 0L, 1L, 0L, 0L);

        assertThat(DistribuicaoRisco.maisCritica(List.of(b, a))).contains(a);
    }

    @Test
    void semRiscoRelevanteNaoHaMaisCritica() {
        DistribuicaoRisco soBaixo = new DistribuicaoRisco("A", 5L, 0L, 0L, 0L, 5L);

        assertThat(DistribuicaoRisco.maisCritica(List.of(soBaixo))).isEmpty();
        assertThat(DistribuicaoRisco.maisCritica(List.of())).isEmpty();
    }

    @Test
    void sentimentoMedioPonderaAsQuantidades() {
        assertThat(totais(9, 1, 0).sentimentoMedio()).isEqualTo(SentimentoReuniao.POSITIVO);
        assertThat(totais(1, 1, 1).sentimentoMedio()).isEqualTo(SentimentoReuniao.NEUTRO);
        assertThat(totais(0, 1, 9).sentimentoMedio()).isEqualTo(SentimentoReuniao.NEGATIVO);
    }

    @Test
    void semAnalisesOSentimentoMedioENegativo() {
        assertThat(totais(0, 0, 0).sentimentoMedio()).isEqualTo(SentimentoReuniao.NEGATIVO);
        assertThat(new TotaisAnalises(0L, null, null, null, null, null, null, null, 0.0).sentimentoMedio())
            .isEqualTo(SentimentoReuniao.NEGATIVO);
    }

    @Test
    void paginacaoCalculaODeslocamento() {
        assertThat(new Paginacao(3, 10, List.of()).deslocamento()).isEqualTo(30);
    }

    private TotaisAnalises totais(long positivos, long neutros, long negativos) {
        return new TotaisAnalises(positivos + neutros + negativos, 0L, 0L, 0L, 0L, positivos, neutros, negativos, 0.0);
    }
}
