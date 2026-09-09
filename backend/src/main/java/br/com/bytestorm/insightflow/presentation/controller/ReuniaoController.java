package br.com.bytestorm.insightflow.presentation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.application.service.ReuniaoService;

@RestController
@RequestMapping("/api/reunioes")
public class ReuniaoController {

    private static final Logger log = LoggerFactory.getLogger(ReuniaoController.class);

    private final ReuniaoService reuniaoService;

    public ReuniaoController(ReuniaoService reuniaoService) {
        this.reuniaoService = reuniaoService;
    }

    @GetMapping
    public ResponseEntity<Page<ReuniaoResponse>> buscarReunioes(Pageable pageable) {
        log.info("Requisição recebida: GET /api/reunioes - page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ReuniaoResponse> reunioes = this.reuniaoService.buscarReunioes(pageable);
        ResponseEntity<Page<ReuniaoResponse>> response = ResponseEntity.status(HttpStatus.OK).body(reunioes);

        log.info("Resposta enviada: GET /api/reunioes - status={}, totalElementos={}", response.getStatusCode(), reunioes.getTotalElements());
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReuniaoResponse> buscarReuniaoPorId(@PathVariable Long id) {
        log.info("Requisição recebida: GET /api/reunioes/{}", id);

        ReuniaoResponse reuniao = this.reuniaoService.buscarReuniaoPorId(id);
        ResponseEntity<ReuniaoResponse> response = ResponseEntity.status(HttpStatus.OK).body(reuniao);

        log.info("Resposta enviada: GET /api/reunioes/{} - status={}", id, response.getStatusCode());
        return response;
    }

}
