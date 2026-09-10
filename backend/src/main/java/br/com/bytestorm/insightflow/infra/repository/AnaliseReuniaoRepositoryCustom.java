package br.com.bytestorm.insightflow.infra.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseCsvRow;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseListaResponse;
import br.com.bytestorm.insightflow.application.dto.response.DistribuicaoRiscoResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasGlobais;

/**
 * Consultas de analise que fogem dos metodos derivados: projecoes DTO e
 * agregacoes feitas no banco (sem hidratar entidade, sem N+1).
 */
public interface AnaliseReuniaoRepositoryCustom {

    /** Pagina da listagem, projetada direto para {@link AnaliseListaResponse} (1 query + 1 count). */
    Page<AnaliseListaResponse> buscarResumo(AnaliseFiltroRequest filtro, Pageable pageable);

    /** Agregados globais (COUNT / conditional COUNT / AVG) numa unica linha. */
    MetricasGlobais metricasGlobais(AnaliseFiltroRequest filtro);

    /** Distribuicao de risco por produto (GROUP BY nome), ordenada por total desc, nome asc. */
    List<DistribuicaoRiscoResponse> distribuicaoRiscoPorProduto(AnaliseFiltroRequest filtro);

    /** Distribuicao de risco por segmento (inner join -> ignora analises sem segmento). */
    List<DistribuicaoRiscoResponse> distribuicaoRiscoPorSegmento(AnaliseFiltroRequest filtro);

    /** Linhas da exportacao CSV, projetadas direto do banco (1 query). */
    List<AnaliseCsvRow> buscarLinhasCsv(AnaliseFiltroRequest filtro);
}
