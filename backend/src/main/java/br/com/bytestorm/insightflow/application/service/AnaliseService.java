package br.com.bytestorm.insightflow.application.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import br.com.bytestorm.insightflow.application.dto.response.DistribuicaoRiscoResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasResponse;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
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

    public AnaliseService(
        AiClient aiClient, ReuniaoService reuniaoService,
        ProdutoTotvsService produtoTotvsService,
        AnaliseReuniaoRepository analiseReuniaoRepository
    ) {
        this.aiClient = aiClient;
        this.reuniaoService = reuniaoService;
        this.produtoTotvsService = produtoTotvsService;
        this.analiseReuniaoRepository = analiseReuniaoRepository;
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

        AnaliseIAResult resultadoIA = aiClient.analisarReuniao(reuniao.getTranscricaoBruta(), produtosDisponiveis);

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
            .produtoTotvs(produtoTotvsService.buscarPorNome(analiseIAResult.produtoTotvsNome()))
            .reuniao(reuniao)
            .build();
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

        MetricasResponse metricas = calcularMetricas(this.analiseReuniaoRepository.findAll(spec));

        return new AnaliseComMetricasResponse(metricas, analises);
    }

    private MetricasResponse calcularMetricas(List<AnaliseReuniao> analises) {
        return new MetricasResponse(
            (long) analises.size(),
            contarPorRisco(analises, RiscoCancelamento.MUITO_ALTO),
            contarPorRisco(analises, RiscoCancelamento.ALTO),
            contarPorRisco(analises, RiscoCancelamento.MODERADO),
            contarPorRisco(analises, RiscoCancelamento.BAIXO),
            calcularMediaSentimento(analises),
            calcularMediaNota(analises),
            contarPorSentimento(analises, SentimentoReuniao.POSITIVO),
            contarPorSentimento(analises, SentimentoReuniao.NEUTRO),
            contarPorSentimento(analises, SentimentoReuniao.NEGATIVO),
            distribuirRiscoPorProduto(analises),
            distribuirRiscoPorSegmento(analises)
        );
    }

    private List<DistribuicaoRiscoResponse> distribuirRiscoPorProduto(List<AnaliseReuniao> analises) {
        Map<String, List<AnaliseReuniao>> porProduto = analises.stream()
            .filter(a -> a.getProdutoTotvs() != null)
            .collect(Collectors.groupingBy(a -> a.getProdutoTotvs().getNome()));

        return distribuirRisco(porProduto);
    }

    private List<DistribuicaoRiscoResponse> distribuirRiscoPorSegmento(List<AnaliseReuniao> analises) {
        Map<String, List<AnaliseReuniao>> porSegmento = analises.stream()
            .filter(a -> a.getReuniao() != null && a.getReuniao().getSegmentoCliente() != null)
            .collect(Collectors.groupingBy(a -> a.getReuniao().getSegmentoCliente().getNome()));

        return distribuirRisco(porSegmento);
    }

    private List<DistribuicaoRiscoResponse> distribuirRisco(Map<String, List<AnaliseReuniao>> grupos) {
        return grupos.entrySet().stream()
            .map(entrada -> {
                List<AnaliseReuniao> lista = entrada.getValue();
                return new DistribuicaoRiscoResponse(
                    entrada.getKey(),
                    (long) lista.size(),
                    contarPorRisco(lista, RiscoCancelamento.MUITO_ALTO),
                    contarPorRisco(lista, RiscoCancelamento.ALTO),
                    contarPorRisco(lista, RiscoCancelamento.MODERADO),
                    contarPorRisco(lista, RiscoCancelamento.BAIXO)
                );
            })
            .sorted(Comparator.comparingLong(DistribuicaoRiscoResponse::total).reversed()
                .thenComparing(DistribuicaoRiscoResponse::rotulo))
            .toList();
    }

    private Long contarPorRisco(List<AnaliseReuniao> analises, RiscoCancelamento risco) {
        return analises.stream()
            .filter(a -> a.getRiscoCancelamento() == risco)
            .count();
    }

    private Long contarPorSentimento(List<AnaliseReuniao> analises, SentimentoReuniao sentimento) {
        return analises.stream()
            .filter(a -> a.getSentimentoReuniao() == sentimento)
            .count();
    }

    private SentimentoReuniao calcularMediaSentimento(List<AnaliseReuniao> analises) {
        long positivos = contarPorSentimento(analises, SentimentoReuniao.POSITIVO);
        long neutros = contarPorSentimento(analises, SentimentoReuniao.NEUTRO);
        long negativos = contarPorSentimento(analises, SentimentoReuniao.NEGATIVO);
        long total = positivos + neutros + negativos;

        if (total == 0) {
            return SentimentoReuniao.fromValor(0.0);
        }

        double somaValores = (positivos * SentimentoReuniao.POSITIVO.getValor())
            + (neutros * SentimentoReuniao.NEUTRO.getValor())
            + (negativos * SentimentoReuniao.NEGATIVO.getValor());

        return SentimentoReuniao.fromValor(somaValores / total);
    }

    private Double calcularMediaNota(List<AnaliseReuniao> analises) {
        return analises.stream()
            .map(AnaliseReuniao::getNota)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
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
