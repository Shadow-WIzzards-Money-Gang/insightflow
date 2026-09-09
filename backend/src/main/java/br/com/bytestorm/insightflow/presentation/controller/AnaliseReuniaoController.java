package br.com.bytestorm.insightflow.presentation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseFiltroRequest;
import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseComMetricasResponse;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
import br.com.bytestorm.insightflow.application.service.AnaliseService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/analises")
public class AnaliseReuniaoController {

    private static final Logger log = LoggerFactory.getLogger(AnaliseReuniaoController.class);

    private final AnaliseService analiseService;

    public AnaliseReuniaoController(AnaliseService analiseService) {
        this.analiseService = analiseService;
    }

    @PostMapping
    public ResponseEntity<AnaliseResponse> analisarReuniao(@Valid @RequestBody AnaliseRequest request) {
        log.info("Requisição recebida: POST /api/analises - segmentoClienteId={}", request.segmentoClienteId());

        AnaliseResponse analise = this.analiseService.analisarReuniao(request);
        ResponseEntity<AnaliseResponse> response = ResponseEntity.status(HttpStatus.CREATED).body(analise);

        log.info("Resposta enviada: POST /api/analises - status={}, analiseId={}", response.getStatusCode(), analise.id());
        return response;
    }

    @GetMapping
    public ResponseEntity<AnaliseComMetricasResponse> buscarAnalises(
        @ModelAttribute AnaliseFiltroRequest filtro,
        Pageable pageable
    ) {
        log.info("Requisição recebida: GET /api/analises - filtro={}, page={}, size={}", filtro, pageable.getPageNumber(), pageable.getPageSize());

        AnaliseComMetricasResponse resultado = this.analiseService.buscarAnalises(pageable, filtro);
        ResponseEntity<AnaliseComMetricasResponse> response = ResponseEntity.status(HttpStatus.OK).body(resultado);

        log.info("Resposta enviada: GET /api/analises - status={}, totalElementos={}", response.getStatusCode(), resultado.analises().getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnaliseResponse> buscarAnalisePorId(@PathVariable Long id) {
        log.info("Requisição recebida: GET /api/analises/{}", id);

        AnaliseResponse analise = this.analiseService.buscarAnalisePorId(id);
        ResponseEntity<AnaliseResponse> response = ResponseEntity.status(HttpStatus.OK).body(analise);

        log.info("Resposta enviada: GET /api/analises/{} - status={}", id, response.getStatusCode());
        return response;
    }

}
