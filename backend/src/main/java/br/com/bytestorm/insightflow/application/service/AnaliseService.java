package br.com.bytestorm.insightflow.application.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseComMetricasResponse;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseListaResponse;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasResponse;
import br.com.bytestorm.insightflow.application.mapper.PaginaMapper;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.exceptions.ia.RespostaIAInvalidaException;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoNaoEncontradoException;
import br.com.bytestorm.insightflow.domain.exceptions.reuniao.ReuniaoNaoEncontradaException;
import br.com.bytestorm.insightflow.domain.gateway.AnalisadorReuniaoGateway;
import br.com.bytestorm.insightflow.domain.repository.AnaliseReuniaoRepository;
import br.com.bytestorm.insightflow.domain.vo.AnaliseExportacao;
import br.com.bytestorm.insightflow.domain.vo.AnaliseResumo;
import br.com.bytestorm.insightflow.domain.vo.DistribuicaoRisco;
import br.com.bytestorm.insightflow.domain.vo.FiltroAnalise;
import br.com.bytestorm.insightflow.domain.vo.HashTranscricao;
import br.com.bytestorm.insightflow.domain.vo.Pagina;
import br.com.bytestorm.insightflow.domain.vo.ResultadoAnaliseIA;
import br.com.bytestorm.insightflow.domain.vo.TotaisAnalises;

@Service
public class AnaliseService {

    private static final Logger log = LoggerFactory.getLogger(AnaliseService.class);

    private final AnaliseReuniaoRepository analiseReuniaoRepository;
    private final AnalisadorReuniaoGateway analisadorReuniao;
    private final ReuniaoService reuniaoService;
    private final ProdutoTotvsService produtoTotvsService;
    private final MetricaService metricaService;
    private final TranscricaoCleaner transcricaoCleaner;
    private final CsvService csvService;

    public AnaliseService(
        AnalisadorReuniaoGateway analisadorReuniao, ReuniaoService reuniaoService,
        ProdutoTotvsService produtoTotvsService,
        AnaliseReuniaoRepository analiseReuniaoRepository,
        MetricaService metricaService,
        TranscricaoCleaner transcricaoCleaner,
        CsvService csvService
    ) {
        this.analisadorReuniao = analisadorReuniao;
        this.reuniaoService = reuniaoService;
        this.produtoTotvsService = produtoTotvsService;
        this.analiseReuniaoRepository = analiseReuniaoRepository;
        this.metricaService = metricaService;
        this.transcricaoCleaner = transcricaoCleaner;
        this.csvService = csvService;
    }

    private static final List<String> CABECALHO_CSV = List.of(
        "id", "assunto", "pontos_positivos", "pontos_negativos", "nota",
        "sentimento_reuniao", "risco_cancelamento", "motivo_cancelamento",
        "produto_totvs", "segmento_cliente", "data_reuniao", "duracao",
        "hash_transcricao", "transcricao_bruta"
    );

    @Transactional
    public AnaliseResponse analisarReuniao(AnaliseRequest request) {

        HashTranscricao hashTranscricao = HashTranscricao.de(request.transcricaoBruta());
        Optional<Reuniao> reuniaoExistente = reuniaoService.buscarPorHashTranscricao(hashTranscricao);

        if(reuniaoExistente.isPresent()) {
            return buscarAnalisePorIdReuniao(reuniaoExistente.get().getId());
        }

        Reuniao reuniao = reuniaoService.cadastrarReuniao(request);

        String produtosDisponiveis = produtoTotvsService.buscarTodos().stream()
                .map((p) -> p.nome().toUpperCase())
                .collect(Collectors.joining(", "));

        String transcricaoLimpa = transcricaoCleaner.limpar(reuniao.getTranscricaoBruta());

        ResultadoAnaliseIA resultadoIA = analisadorReuniao.analisar(transcricaoLimpa, produtosDisponiveis);

        AnaliseReuniao analiseReuniao = AnaliseReuniao.aPartirDe(
            resultadoIA, reuniao, resolverProdutoTotvs(resultadoIA.produtoTotvsNome())
        );

        log.info("Consultando banco de dados - salvando análise da reunião id={}", reuniao.getId());
        AnaliseReuniao analiseSalva = analiseReuniaoRepository.salvar(analiseReuniao);
        log.info("Consulta finalizada - análise id={} salva", analiseSalva.getId());

        return AnaliseResponse.fromEntity(analiseSalva);
    }

    private ProdutoTotvs resolverProdutoTotvs(String nomeIndicadoPelaIA) {
        try {
            return produtoTotvsService.buscarPorNome(nomeIndicadoPelaIA);
        } catch (ProdutoNaoEncontradoException e) {
            throw new RespostaIAInvalidaException(
                "A IA indicou um produto TOTVS que nao existe no catalogo: " + nomeIndicadoPelaIA);
        }
    }

    @Transactional(readOnly = true)
    public AnaliseComMetricasResponse buscarAnalises(Pageable pageable, AnaliseFiltroRequest filtroRequest) {
        FiltroAnalise filtro = filtroRequest.toFiltro();

        log.info("Consultando banco de dados - listando análises (page={}, size={})", pageable.getPageNumber(), pageable.getPageSize());
        Pagina<AnaliseResumo> pagina = this.analiseReuniaoRepository.buscarResumo(filtro, PaginaMapper.toPaginacao(pageable));
        Page<AnaliseListaResponse> analises = PaginaMapper.toPage(pagina.mapear(AnaliseListaResponse::from), pageable);
        log.info("Consulta finalizada - {} análise(s) na página, {} no total", analises.getNumberOfElements(), analises.getTotalElements());

        log.info("Consultando banco de dados - agregando métricas");
        TotaisAnalises totais = this.analiseReuniaoRepository.totais(filtro);
        List<DistribuicaoRisco> porProduto = this.analiseReuniaoRepository.distribuicaoRiscoPorProduto(filtro);
        List<DistribuicaoRisco> porSegmento = this.analiseReuniaoRepository.distribuicaoRiscoPorSegmento(filtro);
        MetricasResponse metricas = metricaService.montar(totais, porProduto, porSegmento);
        log.info("Consulta finalizada - métricas agregadas");

        return new AnaliseComMetricasResponse(metricas, analises);
    }

    @Transactional(readOnly = true)
    public String exportarAnalisesCsv(AnaliseFiltroRequest filtro) {
        log.info("Consultando banco de dados - exportando análises em CSV (filtro={})", filtro);
        List<AnaliseExportacao> linhas = this.analiseReuniaoRepository.buscarParaExportacao(filtro.toFiltro());
        log.info("Consulta finalizada - {} análise(s) para exportação", linhas.size());

        return csvService.gerar(
            CABECALHO_CSV,
            linhas.stream().map(csvService::montarLinhaCsv).toList()
        );
    }

    @Transactional(readOnly = true)
    public AnaliseResponse buscarAnalisePorId(Long id) {
        log.info("Consultando banco de dados - buscando análise id={}", id);
        AnaliseResponse analise = this.analiseReuniaoRepository.buscarPorId(id)
            .map(a -> AnaliseResponse.fromEntity(a))
            .orElseThrow(() -> new ReuniaoNaoEncontradaException());
        log.info("Consulta finalizada - análise id={} encontrada", id);

        return analise;
    }

    public AnaliseResponse buscarAnalisePorIdReuniao(Long id) {
        log.info("Consultando banco de dados - buscando análise pela reunião id={}", id);
        AnaliseResponse analise = AnaliseResponse.fromEntity(this.analiseReuniaoRepository.buscarPorReuniaoId(id).orElseThrow(
            () -> new ReuniaoNaoEncontradaException()
        ));
        log.info("Consulta finalizada - análise da reunião id={} encontrada", id);

        return analise;
    }
}
