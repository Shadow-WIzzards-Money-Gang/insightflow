package br.com.bytestorm.insightflow.infra.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bytestorm.insightflow.infra.persistence.entity.ReuniaoJpaEntity;

public interface ReuniaoJpaRepository extends JpaRepository<ReuniaoJpaEntity, Long> {

    Boolean existsByHashTranscricao(String hashTranscricao);
    Optional<ReuniaoJpaEntity> findByHashTranscricao(String hashTranscricao);

}
