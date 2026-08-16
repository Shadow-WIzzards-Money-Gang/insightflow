package br.com.bytestorm.insightflow.infra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;

public interface AnaliseReuniaoRepository extends JpaRepository<AnaliseReuniao, Long> {

    Optional<AnaliseReuniao> findByReuniaoId(Long id);    

}
