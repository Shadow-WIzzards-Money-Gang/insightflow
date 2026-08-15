package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.request.ProdutoTotvsRequest;
import br.com.bytestorm.insightflow.application.dto.response.ProdutoTotvsResponse;
import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoJaCadastradoException;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoNaoEncontradoException;
import br.com.bytestorm.insightflow.infra.repository.ProdutoTotvsRepository;

@Service
public class ProdutoTotvsService {

    private final ProdutoTotvsRepository produtoTotvsRepository;

    public ProdutoTotvsService(ProdutoTotvsRepository produtoTotvsRepository) {
        this.produtoTotvsRepository = produtoTotvsRepository;
    }

    public void cadastrarProdutoTotvs(ProdutoTotvsRequest request) {

        if (produtoTotvsRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new ProdutoJaCadastradoException();
        }

        produtoTotvsRepository.save(request.toEntity());
    }

    public List<ProdutoTotvsResponse> buscarTodos() {
        return produtoTotvsRepository.findAll().stream()
            .map((p) -> ProdutoTotvsResponse.fromEntity(p))
            .toList();
    }

    public List<ProdutoTotvsResponse> buscarPorCategoria(String categoria) {
        return produtoTotvsRepository.findByCategoriaIgnoreCase(categoria).stream()
                .map((p) -> ProdutoTotvsResponse.fromEntity(p))
                .toList();
    }

    public ProdutoTotvs buscarPorNome(String nome) {
        return produtoTotvsRepository.findByNomeIgnoreCase(nome).orElseThrow(
            () -> new ProdutoNaoEncontradoException()
        );
    }

    public void deletarProdutoTotvs(Long id) {
        ProdutoTotvs produto = buscaPorId(id);
        produtoTotvsRepository.delete(produto);
    }

    public ProdutoTotvs buscaPorId(Long id) {
        return produtoTotvsRepository.findById(id).orElseThrow(
            () -> new ProdutoNaoEncontradoException()
        );
    }

}
