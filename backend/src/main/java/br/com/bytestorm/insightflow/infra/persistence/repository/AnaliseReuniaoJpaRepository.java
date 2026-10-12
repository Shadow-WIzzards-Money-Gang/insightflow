package br.com.bytestorm.insightflow.infra.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bytestorm.insightflow.infra.persistence.entity.AnaliseReuniaoJpaEntity;

public interface AnaliseReuniaoJpaRepository extends JpaRepository<AnaliseReuniaoJpaEntity, Long> {

    Optional<AnaliseReuniaoJpaEntity> findByReuniaoId(Long id);

}
