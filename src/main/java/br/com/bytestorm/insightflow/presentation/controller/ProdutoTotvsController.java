package br.com.bytestorm.insightflow.presentation.controller;

import java.util.List;

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

import br.com.bytestorm.insightflow.application.dto.ProdutoTotvsDTO;
import br.com.bytestorm.insightflow.application.service.ProdutoTotvsService;
import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoTotvsController {

    private final ProdutoTotvsService produtoTotvsService;

    public ProdutoTotvsController(ProdutoTotvsService produtoTotvsService) {
        this.produtoTotvsService = produtoTotvsService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoTotvs>> listarProdutosTotvs() {
        return ResponseEntity.status(HttpStatus.OK).body(produtoTotvsService.buscarTodos());
    }

    @GetMapping(params = "categoria")
    public ResponseEntity<List<ProdutoTotvs>> listarProdutoTotvsPorCategoria(@RequestParam String categoria) {
        return ResponseEntity.status(HttpStatus.OK).body(produtoTotvsService.buscarPorCategoria(categoria));
    }

    @PostMapping
    public ResponseEntity<Void> cadastrarProdutoTotvs(@RequestBody @Valid ProdutoTotvsDTO request) {
        produtoTotvsService.cadastrarProdutoTotvs(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProdutoTotvs(@PathVariable Long id) {
        produtoTotvsService.deletarProdutoTotvs(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
