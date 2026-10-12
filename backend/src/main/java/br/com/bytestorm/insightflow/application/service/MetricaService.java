package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.response.DistribuicaoRiscoResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasResponse;
import br.com.bytestorm.insightflow.domain.vo.DistribuicaoRisco;
import br.com.bytestorm.insightflow.domain.vo.TotaisAnalises;

/**
 * Monta o {@link MetricasResponse} a partir dos agregados ja calculados no banco
 * ({@link TotaisAnalises} + as duas distribuicoes de risco). As regras (sentimento
 * medio, produto/segmento mais critico) ficam no dominio; aqui so se monta a resposta.
 */
@Service
public class MetricaService {

    public MetricasResponse montar(
        TotaisAnalises totais,
        List<DistribuicaoRisco> riscoPorProduto,
        List<DistribuicaoRisco> riscoPorSegmento
    ) {
        return new MetricasResponse(
            totais.totalReunioes(),
            totais.muitoAlto(),
            totais.alto(),
            totais.moderado(),
            totais.baixo(),
            totais.sentimentoMedio(),
            totais.notaMedia(),
            totais.sentPositivo(),
            totais.sentNeutro(),
            totais.sentNegativo(),
            riscoPorProduto.stream().map(DistribuicaoRiscoResponse::from).toList(),
            riscoPorSegmento.stream().map(DistribuicaoRiscoResponse::from).toList(),
            maisCritico(riscoPorProduto),
            maisCritico(riscoPorSegmento)
        );
    }

    private DistribuicaoRiscoResponse maisCritico(List<DistribuicaoRisco> distribuicoes) {
        return DistribuicaoRisco.maisCritica(distribuicoes)
            .map(DistribuicaoRiscoResponse::from)
            .orElse(null);
    }
}
