package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.request.ProdutoTotvsRequest;
import br.com.bytestorm.insightflow.application.dto.response.ProdutoTotvsResponse;
import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoJaCadastradoException;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoNaoEncontradoException;
import br.com.bytestorm.insightflow.domain.repository.ProdutoTotvsRepository;

@Service
public class ProdutoTotvsService {

    private static final Logger log = LoggerFactory.getLogger(ProdutoTotvsService.class);

    private final ProdutoTotvsRepository produtoTotvsRepository;

    public ProdutoTotvsService(ProdutoTotvsRepository produtoTotvsRepository) {
        this.produtoTotvsRepository = produtoTotvsRepository;
    }

    public void cadastrarProdutoTotvs(ProdutoTotvsRequest request) {
        log.info("Consultando banco de dados - verificando existência do produto '{}'", request.nome());
        boolean jaExiste = produtoTotvsRepository.existePorNome(request.nome());
        log.info("Consulta finalizada - produto '{}' já existe: {}", request.nome(), jaExiste);

        if (jaExiste) {
            throw new ProdutoJaCadastradoException();
        }

        log.info("Consultando banco de dados - salvando produto '{}'", request.nome());
        produtoTotvsRepository.salvar(request.toEntity());
        log.info("Consulta finalizada - produto '{}' salvo", request.nome());
    }

    public List<ProdutoTotvsResponse> buscarTodos() {
        log.info("Consultando banco de dados - buscando todos os produtos");
        List<ProdutoTotvsResponse> produtos = produtoTotvsRepository.buscarTodos().stream()
            .map((p) -> ProdutoTotvsResponse.fromEntity(p))
            .toList();
        log.info("Consulta finalizada - {} produtos encontrados", produtos.size());

        return produtos;
    }

    public List<ProdutoTotvsResponse> buscarPorCategoria(String categoria) {
        log.info("Consultando banco de dados - buscando produtos da categoria '{}'", categoria);
        List<ProdutoTotvsResponse> produtos = produtoTotvsRepository.buscarPorCategoria(categoria).stream()
                .map((p) -> ProdutoTotvsResponse.fromEntity(p))
                .toList();
        log.info("Consulta finalizada - {} produtos encontrados na categoria '{}'", produtos.size(), categoria);

        return produtos;
    }

    public ProdutoTotvs buscarPorNome(String nome) {
        log.info("Consultando banco de dados - buscando produto por nome '{}'", nome);
        ProdutoTotvs produto = produtoTotvsRepository.buscarPorNome(nome).orElseThrow(
            () -> new ProdutoNaoEncontradoException()
        );
        log.info("Consulta finalizada - produto '{}' encontrado", nome);

        return produto;
    }

    public void deletarProdutoTotvs(Long id) {
        ProdutoTotvs produto = buscaPorId(id);

        log.info("Consultando banco de dados - excluindo produto id={}", id);
        produtoTotvsRepository.remover(produto);
        log.info("Consulta finalizada - produto id={} excluído", id);
    }

    public ProdutoTotvs buscaPorId(Long id) {
        log.info("Consultando banco de dados - buscando produto id={}", id);
        ProdutoTotvs produto = produtoTotvsRepository.buscarPorId(id).orElseThrow(
            () -> new ProdutoNaoEncontradoException()
        );
        log.info("Consulta finalizada - produto id={} encontrado", id);

        return produto;
    }

}
