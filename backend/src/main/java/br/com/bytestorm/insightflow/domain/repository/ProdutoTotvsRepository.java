package br.com.bytestorm.insightflow.domain.repository;

import java.util.List;
import java.util.Optional;

import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;

public interface ProdutoTotvsRepository {

    ProdutoTotvs salvar(ProdutoTotvs produtoTotvs);

    void remover(ProdutoTotvs produtoTotvs);

    List<ProdutoTotvs> buscarTodos();

    List<ProdutoTotvs> buscarPorCategoria(String categoria);

    Optional<ProdutoTotvs> buscarPorId(Long id);

    Optional<ProdutoTotvs> buscarPorNome(String nome);

    boolean existePorNome(String nome);
}
