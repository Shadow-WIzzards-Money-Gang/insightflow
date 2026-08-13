package br.com.bytestorm.insightflow.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.application.service.AnaliseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reunioes")
public class ReuniaoController {

    private final AnaliseService analiseService;
    
    public ReuniaoController(AnaliseService analiseService) {
        this.analiseService = analiseService;
    }

    @PostMapping("/analise")
    public ResponseEntity<AnaliseResponse> analisarReuniao(@Valid @RequestBody AnaliseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.analiseService.analisarReuniao(request));
    }

    @GetMapping("/analise")
    public ResponseEntity<List<AnaliseResponse>> buscarAnalises() {
        return ResponseEntity.status(HttpStatus.OK).body(this.analiseService.buscarAnalises());
    }

    @GetMapping
    public ResponseEntity<List<ReuniaoResponse>> buscarReunioes() {
        return ResponseEntity.status(HttpStatus.OK).body(this.analiseService.buscarReunioes());
    }

}
