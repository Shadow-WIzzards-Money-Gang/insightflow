package br.com.bytestorm.insightflow.application.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bytestorm.insightflow.application.dto.ia.AnaliseIAResult;
import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseComMetricasResponse;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
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
import br.com.bytestorm.insightflow.infra.repository.specification.AnaliseReuniaoSpecification;

@Service
public class AnaliseService {

    private final AnaliseReuniaoRepository analiseReuniaoRepository;
    private final AiClient aiClient;
    private final ReuniaoService reuniaoService;
    private final ProdutoTotvsService produtoTotvsService;
    private final MetricaService metricaService;
    private final TranscricaoCleaner transcricaoCleaner;

    public AnaliseService(
        AiClient aiClient, ReuniaoService reuniaoService,
        ProdutoTotvsService produtoTotvsService,
        AnaliseReuniaoRepository analiseReuniaoRepository,
        MetricaService metricaService,
        TranscricaoCleaner transcricaoCleaner
    ) {
        this.aiClient = aiClient;
        this.reuniaoService = reuniaoService;
        this.produtoTotvsService = produtoTotvsService;
        this.analiseReuniaoRepository = analiseReuniaoRepository;
        this.metricaService = metricaService;
        this.transcricaoCleaner = transcricaoCleaner;
    }

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

        return AnaliseResponse.fromEntity(analiseReuniaoRepository.save(analiseReuniao));
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
        Specification<AnaliseReuniao> spec = AnaliseReuniaoSpecification.comFiltros(filtro);

        Page<AnaliseResponse> analises = this.analiseReuniaoRepository
            .findAll(spec, pageable)
            .map((a) -> Helpers.resumirAnalise(a));

        MetricasResponse metricas = metricaService.calcularMetricas(this.analiseReuniaoRepository.findAll(spec));

        return new AnaliseComMetricasResponse(metricas, analises);
    }

    public AnaliseResponse buscarAnalisePorId(Long id) {
        return this.analiseReuniaoRepository.findById(id)
            .map(a -> AnaliseResponse.fromEntity(a))
            .orElseThrow(() -> new ReuniaoNaoEncontradaException());
    }

    public AnaliseResponse buscarAnalisePorIdReuniao(Long id) {
        return AnaliseResponse.fromEntity(this.analiseReuniaoRepository.findByReuniaoId(id).orElseThrow(
            () -> new ReuniaoNaoEncontradaException()
        ));
    }
}
