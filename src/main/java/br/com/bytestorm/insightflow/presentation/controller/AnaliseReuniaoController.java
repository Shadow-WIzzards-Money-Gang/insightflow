package br.com.bytestorm.insightflow.presentation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
import br.com.bytestorm.insightflow.application.dto.response.MetricasResponse;
import br.com.bytestorm.insightflow.application.service.AnaliseService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/analises")
public class AnaliseReuniaoController {

    private final AnaliseService analiseService;

    public AnaliseReuniaoController(AnaliseService analiseService) {
        this.analiseService = analiseService;
    }

    @PostMapping
    public ResponseEntity<AnaliseResponse> analisarReuniao(@Valid @RequestBody AnaliseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.analiseService.analisarReuniao(request));
    }

    @GetMapping
    public ResponseEntity<Page<AnaliseResponse>> buscarAnalises(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.analiseService.buscarAnalises(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnaliseResponse> buscarAnalisePorId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(this.analiseService.buscarAnalisePorId(id));
    }

    @GetMapping("/metricas")
    public ResponseEntity<MetricasResponse> buscarMetricas() {
        return ResponseEntity.status(HttpStatus.OK).body(this.analiseService.buscarMetricas());
    }
    

}
