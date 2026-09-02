package br.com.bytestorm.insightflow.infra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;


public interface AnaliseReuniaoRepository
        extends JpaRepository<AnaliseReuniao, Long>, JpaSpecificationExecutor<AnaliseReuniao> {

    Optional<AnaliseReuniao> findByReuniaoId(Long id);

}
