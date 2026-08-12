package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.ProdutoTotvsDTO;
import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoJaCadastrado;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoNaoEncontrado;
import br.com.bytestorm.insightflow.infra.repository.ProdutoTotvsRepository;

@Service
public class ProdutoTotvsService {

    private final ProdutoTotvsRepository produtoTotvsRepository;

    public ProdutoTotvsService(ProdutoTotvsRepository produtoTotvsRepository) {
        this.produtoTotvsRepository = produtoTotvsRepository;
    }

    public void cadastrarProdutoTotvs(ProdutoTotvsDTO request) {

        if (produtoTotvsRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new ProdutoJaCadastrado();
        }

        produtoTotvsRepository.save(request.toEntity());
    }

    public List<ProdutoTotvs> buscarTodos() {
        return produtoTotvsRepository.findAll();
    }

    public List<ProdutoTotvs> buscarPorCategoria(String categoria) {
        return produtoTotvsRepository.findByCategoriaIgnoreCase(categoria);
    }

    public void deletarProdutoTotvs(Long id) {
        ProdutoTotvs produto = buscaPorId(id);
        produtoTotvsRepository.delete(produto);
    }

    public ProdutoTotvs buscaPorId(Long id) {
        return produtoTotvsRepository.findById(id).orElseThrow(
            () -> new ProdutoNaoEncontrado()
        );
    }

}
