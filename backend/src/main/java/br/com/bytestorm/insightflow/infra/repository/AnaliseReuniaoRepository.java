package br.com.bytestorm.insightflow.infra.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;


public interface AnaliseReuniaoRepository
        extends JpaRepository<AnaliseReuniao, Long>, JpaSpecificationExecutor<AnaliseReuniao> {

    Optional<AnaliseReuniao> findByReuniaoId(Long id);

    Long countByRiscoCancelamento(RiscoCancelamento riscoCancelamento);

    Long countBySentimentoReuniao(SentimentoReuniao sentimentoReuniao);

    @Query("SELECT AVG(a.nota) FROM AnaliseReuniao a")
    Double calcularMediaNota();

}
