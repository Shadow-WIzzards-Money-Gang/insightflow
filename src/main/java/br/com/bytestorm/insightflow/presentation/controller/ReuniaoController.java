package br.com.bytestorm.insightflow.presentation.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.application.service.ReuniaoService;

@RestController
@RequestMapping("/api/reunioes")
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    public ReuniaoController(ReuniaoService reuniaoService) {
        this.reuniaoService = reuniaoService;
    }

    @GetMapping
    public ResponseEntity<Page<ReuniaoResponse>> buscarReunioes(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.reuniaoService.buscarReunioes(pageable));
    }

}
