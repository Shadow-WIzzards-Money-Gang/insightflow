package br.com.bytestorm.insightflow.infra.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.domain.repository.ProdutoTotvsRepository;
import br.com.bytestorm.insightflow.infra.persistence.mapper.ProdutoTotvsMapper;
import br.com.bytestorm.insightflow.infra.persistence.repository.ProdutoTotvsJpaRepository;

@Repository
@Transactional(readOnly = true)
public class ProdutoTotvsRepositoryAdapter implements ProdutoTotvsRepository {

    private final ProdutoTotvsJpaRepository jpaRepository;

    public ProdutoTotvsRepositoryAdapter(ProdutoTotvsJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public ProdutoTotvs salvar(ProdutoTotvs produtoTotvs) {
        return ProdutoTotvsMapper.toDomain(jpaRepository.save(ProdutoTotvsMapper.toJpa(produtoTotvs)));
    }

    @Override
    @Transactional
    public void remover(ProdutoTotvs produtoTotvs) {
        jpaRepository.deleteById(produtoTotvs.getId());
    }

    @Override
    public List<ProdutoTotvs> buscarTodos() {
        return jpaRepository.findAll().stream().map(ProdutoTotvsMapper::toDomain).toList();
    }

    @Override
    public List<ProdutoTotvs> buscarPorCategoria(String categoria) {
        return jpaRepository.findByCategoriaIgnoreCase(categoria).stream().map(ProdutoTotvsMapper::toDomain).toList();
    }

    @Override
    public Optional<ProdutoTotvs> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(ProdutoTotvsMapper::toDomain);
    }

    @Override
    public Optional<ProdutoTotvs> buscarPorNome(String nome) {
        return jpaRepository.findByNomeIgnoreCase(nome).map(ProdutoTotvsMapper::toDomain);
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.existsByNomeIgnoreCase(nome);
    }
}
