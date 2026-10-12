package br.com.bytestorm.insightflow.domain.repository;

import java.util.List;
import java.util.Optional;

import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.vo.AnaliseExportacao;
import br.com.bytestorm.insightflow.domain.vo.AnaliseResumo;
import br.com.bytestorm.insightflow.domain.vo.DistribuicaoRisco;
import br.com.bytestorm.insightflow.domain.vo.FiltroAnalise;
import br.com.bytestorm.insightflow.domain.vo.Pagina;
import br.com.bytestorm.insightflow.domain.vo.Paginacao;
import br.com.bytestorm.insightflow.domain.vo.TotaisAnalises;

public interface AnaliseReuniaoRepository {

    AnaliseReuniao salvar(AnaliseReuniao analiseReuniao);

    Optional<AnaliseReuniao> buscarPorId(Long id);

    Optional<AnaliseReuniao> buscarPorReuniaoId(Long reuniaoId);

    Pagina<AnaliseResumo> buscarResumo(FiltroAnalise filtro, Paginacao paginacao);

    TotaisAnalises totais(FiltroAnalise filtro);

    /** Ordenada por total desc, nome do produto asc. */
    List<DistribuicaoRisco> distribuicaoRiscoPorProduto(FiltroAnalise filtro);

    /** Ordenada por total desc, nome do segmento asc. Ignora analises sem segmento. */
    List<DistribuicaoRisco> distribuicaoRiscoPorSegmento(FiltroAnalise filtro);

    List<AnaliseExportacao> buscarParaExportacao(FiltroAnalise filtro);
}
