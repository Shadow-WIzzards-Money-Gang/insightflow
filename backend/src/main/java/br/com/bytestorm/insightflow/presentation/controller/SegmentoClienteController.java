package br.com.bytestorm.insightflow.presentation.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.request.SegmentoClienteRequest;
import br.com.bytestorm.insightflow.application.dto.response.SegmentoClienteResponse;
import br.com.bytestorm.insightflow.application.service.SegmentoClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/segmentos")
public class SegmentoClienteController {

    private static final Logger log = LoggerFactory.getLogger(SegmentoClienteController.class);

    private final SegmentoClienteService segmentoClienteService;

    public SegmentoClienteController(SegmentoClienteService segmentoClienteService) {
        this.segmentoClienteService = segmentoClienteService;
    }

    @GetMapping
    public ResponseEntity<List<SegmentoClienteResponse>> buscarTodos() {
        log.info("Requisição recebida: GET /api/segmentos");

        List<SegmentoClienteResponse> segmentos = segmentoClienteService.buscarTodos();
        ResponseEntity<List<SegmentoClienteResponse>> response = ResponseEntity.status(HttpStatus.OK).body(segmentos);

        log.info("Resposta enviada: GET /api/segmentos - status={}, total={}", response.getStatusCode(), segmentos.size());
        return response;
    }

    @GetMapping("/{nome}")
    public ResponseEntity<SegmentoClienteResponse> buscarPorNome(@PathVariable String nome) {
        log.info("Requisição recebida: GET /api/segmentos/{}", nome);

        SegmentoClienteResponse segmento = segmentoClienteService.buscarPorNome(nome);
        ResponseEntity<SegmentoClienteResponse> response = ResponseEntity.status(HttpStatus.OK).body(segmento);

        log.info("Resposta enviada: GET /api/segmentos/{} - status={}", nome, response.getStatusCode());
        return response;
    }

    @PostMapping
    public ResponseEntity<Void> cadastrarSegmentoCliente(@RequestBody @Valid SegmentoClienteRequest request) {
        log.info("Requisição recebida: POST /api/segmentos - nome={}", request.nome());

        segmentoClienteService.cadastrarSegmentoCliente(request);
        ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.CREATED).build();

        log.info("Resposta enviada: POST /api/segmentos - status={}", response.getStatusCode());
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSegmentoCliente(@PathVariable Long id) {
        log.info("Requisição recebida: DELETE /api/segmentos/{}", id);

        segmentoClienteService.deletarSegmentoCliente(id);
        ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.OK).build();

        log.info("Resposta enviada: DELETE /api/segmentos/{} - status={}", id, response.getStatusCode());
        return response;
    }

}
