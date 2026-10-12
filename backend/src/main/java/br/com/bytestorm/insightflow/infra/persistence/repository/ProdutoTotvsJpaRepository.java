package br.com.bytestorm.insightflow.infra.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bytestorm.insightflow.infra.persistence.entity.ProdutoTotvsJpaEntity;

public interface ProdutoTotvsJpaRepository extends JpaRepository<ProdutoTotvsJpaEntity, Long> {

    List<ProdutoTotvsJpaEntity> findByCategoriaIgnoreCase(String categoria);
    Boolean existsByNomeIgnoreCase(String nome);
    Optional<ProdutoTotvsJpaEntity> findByNomeIgnoreCase(String nome);

}
