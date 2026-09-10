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

import br.com.bytestorm.insightflow.application.dto.ia.AnaliseIAResult;
import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseComMetricasResponse;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseCsvRow;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseListaResponse;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
import br.com.bytestorm.insightflow.application.dto.response.DistribuicaoRiscoResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasGlobais;
import br.com.bytestorm.insightflow.application.dto.response.MetricasResponse;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.exceptions.ia.RespostaIAInvalidaException;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoNaoEncontradoException;
import br.com.bytestorm.insightflow.domain.exceptions.reuniao.ReuniaoNaoEncontradaException;
import br.com.bytestorm.insightflow.helpers.Helpers;
import br.com.bytestorm.insightflow.infra.ai.AiClient;
import br.com.bytestorm.insightflow.infra.repository.AnaliseReuniaoRepository;

@Service
public class AnaliseService {

    private static final Logger log = LoggerFactory.getLogger(AnaliseService.class);

    private final AnaliseReuniaoRepository analiseReuniaoRepository;
    private final AiClient aiClient;
    private final ReuniaoService reuniaoService;
    private final ProdutoTotvsService produtoTotvsService;
    private final MetricaService metricaService;
    private final TranscricaoCleaner transcricaoCleaner;
    private final CsvService csvService;

    public AnaliseService(
        AiClient aiClient, ReuniaoService reuniaoService,
        ProdutoTotvsService produtoTotvsService,
        AnaliseReuniaoRepository analiseReuniaoRepository,
        MetricaService metricaService,
        TranscricaoCleaner transcricaoCleaner,
        CsvService csvService
    ) {
        this.aiClient = aiClient;
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

        String hashTranscricao = Helpers.gerarHash(request.transcricaoBruta());
        Optional<Reuniao> reuniaoExistente = reuniaoService.buscarPorHashTranscricao(hashTranscricao);

        if(reuniaoExistente.isPresent()) {
            return buscarAnalisePorIdReuniao(reuniaoExistente.get().getId());
        }

        Reuniao reuniao = reuniaoService.cadastrarReuniao(request, hashTranscricao);

        String produtosDisponiveis = produtoTotvsService.buscarTodos().stream()
                .map((p) -> p.nome().toUpperCase())
                .collect(Collectors.joining(", "));

        String transcricaoLimpa = transcricaoCleaner.limpar(reuniao.getTranscricaoBruta());

        AnaliseIAResult resultadoIA = aiClient.analisarReuniao(transcricaoLimpa, produtosDisponiveis);

        AnaliseReuniao analiseReuniao = converterParaEntidade(resultadoIA, reuniao);

        log.info("Consultando banco de dados - salvando análise da reunião id={}", reuniao.getId());
        AnaliseReuniao analiseSalva = analiseReuniaoRepository.save(analiseReuniao);
        log.info("Consulta finalizada - análise id={} salva", analiseSalva.getId());

        return AnaliseResponse.fromEntity(analiseSalva);
    }

    public AnaliseReuniao converterParaEntidade(AnaliseIAResult analiseIAResult, Reuniao reuniao) {
        RiscoCancelamento riscoCancelamento = RiscoCancelamento.fromString(analiseIAResult.riscoCancelamento().toUpperCase());

        return AnaliseReuniao.builder()
            .assunto(analiseIAResult.assunto())
            .pontosPositivos(analiseIAResult.pontosPositivos())
            .pontosNegativos(analiseIAResult.pontosNegativos())
            .nota(analiseIAResult.nota())
            .sentimentoReuniao(SentimentoReuniao.fromString(analiseIAResult.sentimentoReuniao().toUpperCase()))
            .riscoCancelamento(riscoCancelamento)
            .motivoCancelamento(extrairMotivoCancelamento(riscoCancelamento, analiseIAResult.motivoCancelamento()))
            .produtoTotvs(resolverProdutoTotvs(analiseIAResult.produtoTotvsNome()))
            .reuniao(reuniao)
            .build();
    }

    private ProdutoTotvs resolverProdutoTotvs(String nomeIndicadoPelaIA) {
        try {
            return produtoTotvsService.buscarPorNome(nomeIndicadoPelaIA);
        } catch (ProdutoNaoEncontradoException e) {
            throw new RespostaIAInvalidaException(
                "A IA indicou um produto TOTVS que nao existe no catalogo: " + nomeIndicadoPelaIA);
        }
    }

    private String extrairMotivoCancelamento(RiscoCancelamento riscoCancelamento, String motivoCancelamento) {
        if (riscoCancelamento == RiscoCancelamento.BAIXO) {
            return null;
        }
        return (motivoCancelamento == null || motivoCancelamento.isBlank()) ? null : motivoCancelamento;
    }

    @Transactional(readOnly = true)
    public AnaliseComMetricasResponse buscarAnalises(Pageable pageable, AnaliseFiltroRequest filtro) {
        log.info("Consultando banco de dados - listando análises (page={}, size={})", pageable.getPageNumber(), pageable.getPageSize());
        Page<AnaliseListaResponse> analises = this.analiseReuniaoRepository.buscarResumo(filtro, pageable);
        log.info("Consulta finalizada - {} análise(s) na página, {} no total", analises.getNumberOfElements(), analises.getTotalElements());

        log.info("Consultando banco de dados - agregando métricas");
        MetricasGlobais global = this.analiseReuniaoRepository.metricasGlobais(filtro);
        List<DistribuicaoRiscoResponse> porProduto = this.analiseReuniaoRepository.distribuicaoRiscoPorProduto(filtro);
        List<DistribuicaoRiscoResponse> porSegmento = this.analiseReuniaoRepository.distribuicaoRiscoPorSegmento(filtro);
        MetricasResponse metricas = metricaService.montar(global, porProduto, porSegmento);
        log.info("Consulta finalizada - métricas agregadas");

        return new AnaliseComMetricasResponse(metricas, analises);
    }

    @Transactional(readOnly = true)
    public String exportarAnalisesCsv(AnaliseFiltroRequest filtro) {
        log.info("Consultando banco de dados - exportando análises em CSV (filtro={})", filtro);
        List<AnaliseCsvRow> linhas = this.analiseReuniaoRepository.buscarLinhasCsv(filtro);
        log.info("Consulta finalizada - {} análise(s) para exportação", linhas.size());

        return csvService.gerar(
            CABECALHO_CSV,
            linhas.stream().map(csvService::montarLinhaCsv).toList()
        );
    }

    @Transactional(readOnly = true)
    public AnaliseResponse buscarAnalisePorId(Long id) {
        log.info("Consultando banco de dados - buscando análise id={}", id);
        AnaliseResponse analise = this.analiseReuniaoRepository.findById(id)
            .map(a -> AnaliseResponse.fromEntity(a))
            .orElseThrow(() -> new ReuniaoNaoEncontradaException());
        log.info("Consulta finalizada - análise id={} encontrada", id);

        return analise;
    }

    public AnaliseResponse buscarAnalisePorIdReuniao(Long id) {
        log.info("Consultando banco de dados - buscando análise pela reunião id={}", id);
        AnaliseResponse analise = AnaliseResponse.fromEntity(this.analiseReuniaoRepository.findByReuniaoId(id).orElseThrow(
            () -> new ReuniaoNaoEncontradaException()
        ));
        log.info("Consulta finalizada - análise da reunião id={} encontrada", id);

        return analise;
    }
}
