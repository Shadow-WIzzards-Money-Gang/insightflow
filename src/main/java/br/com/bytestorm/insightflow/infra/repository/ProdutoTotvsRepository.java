package br.com.bytestorm.insightflow.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;

public interface ProdutoTotvsRepository extends JpaRepository<ProdutoTotvs, Long> {

}
