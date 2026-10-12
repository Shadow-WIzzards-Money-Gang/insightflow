package br.com.bytestorm.insightflow.infra.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bytestorm.insightflow.infra.persistence.entity.SegmentoClienteJpaEntity;

public interface SegmentoClienteJpaRepository extends JpaRepository<SegmentoClienteJpaEntity, Long> {

    Boolean existsByNomeIgnoreCase(String nome);
    Optional<SegmentoClienteJpaEntity> findByNomeIgnoreCase(String nome);

}
