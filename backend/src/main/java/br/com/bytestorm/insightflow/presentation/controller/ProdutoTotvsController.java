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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.bytestorm.insightflow.application.dto.request.ProdutoTotvsRequest;
import br.com.bytestorm.insightflow.application.dto.response.ProdutoTotvsResponse;
import br.com.bytestorm.insightflow.application.service.ProdutoTotvsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoTotvsController {

    private static final Logger log = LoggerFactory.getLogger(ProdutoTotvsController.class);

    private final ProdutoTotvsService produtoTotvsService;

    public ProdutoTotvsController(ProdutoTotvsService produtoTotvsService) {
        this.produtoTotvsService = produtoTotvsService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoTotvsResponse>> listarProdutosTotvs() {
        log.info("Requisição recebida: GET /api/produtos");

        List<ProdutoTotvsResponse> produtos = produtoTotvsService.buscarTodos();
        ResponseEntity<List<ProdutoTotvsResponse>> response = ResponseEntity.status(HttpStatus.OK).body(produtos);

        log.info("Resposta enviada: GET /api/produtos - status={}, total={}", response.getStatusCode(), produtos.size());
        return response;
    }

    @GetMapping(params = "categoria")
    public ResponseEntity<List<ProdutoTotvsResponse>> listarProdutoTotvsPorCategoria(@RequestParam String categoria) {
        log.info("Requisição recebida: GET /api/produtos?categoria={}", categoria);

        List<ProdutoTotvsResponse> produtos = produtoTotvsService.buscarPorCategoria(categoria);
        ResponseEntity<List<ProdutoTotvsResponse>> response = ResponseEntity.status(HttpStatus.OK).body(produtos);

        log.info("Resposta enviada: GET /api/produtos?categoria={} - status={}, total={}", categoria, response.getStatusCode(), produtos.size());
        return response;
    }

    @PostMapping
    public ResponseEntity<Void> cadastrarProdutoTotvs(@RequestBody @Valid ProdutoTotvsRequest request) {
        log.info("Requisição recebida: POST /api/produtos - nome={}", request.nome());

        produtoTotvsService.cadastrarProdutoTotvs(request);
        ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.CREATED).build();

        log.info("Resposta enviada: POST /api/produtos - status={}", response.getStatusCode());
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProdutoTotvs(@PathVariable Long id) {
        log.info("Requisição recebida: DELETE /api/produtos/{}", id);

        produtoTotvsService.deletarProdutoTotvs(id);
        ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.OK).build();

        log.info("Resposta enviada: DELETE /api/produtos/{} - status={}", id, response.getStatusCode());
        return response;
    }

}
