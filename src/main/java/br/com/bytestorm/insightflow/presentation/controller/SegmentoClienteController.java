package br.com.bytestorm.insightflow.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.SegmentoClienteDTO;
import br.com.bytestorm.insightflow.application.service.SegmentoClienteService;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/segmentos")
public class SegmentoClienteController {

    private final SegmentoClienteService segmentoClienteService;

    public SegmentoClienteController(SegmentoClienteService segmentoClienteService) {
        this.segmentoClienteService = segmentoClienteService;
    }

    @GetMapping
    public ResponseEntity<List<SegmentoCliente>> buscarTodos() {
        return ResponseEntity.status(HttpStatus.OK).body(segmentoClienteService.buscarTodos());
    }

    @PostMapping
    public ResponseEntity<Void> cadastrarSegmentoCliente(@RequestBody @Valid SegmentoClienteDTO request) {
        segmentoClienteService.cadastrarSegmentoCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
