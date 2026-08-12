package br.com.bytestorm.insightflow.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.request.ReuniaoRequest;
import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.application.service.ReuniaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reunioes")
public class ReuniaoController {

    private final ReuniaoService reuniaoService;
    
    public ReuniaoController(ReuniaoService reuniaoService) {
        this.reuniaoService = reuniaoService;
    }

    @PostMapping
    public ResponseEntity<Void> criarReuniao(@Valid @RequestBody ReuniaoRequest request) {
        this.reuniaoService.criarReuniao(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ReuniaoResponse>> buscarTodos() {
        return ResponseEntity.status(HttpStatus.OK).body(this.reuniaoService.buscarTodos());
    }

}
