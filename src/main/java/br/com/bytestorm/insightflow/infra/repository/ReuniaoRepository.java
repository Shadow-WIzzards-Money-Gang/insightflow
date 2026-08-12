package br.com.bytestorm.insightflow.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bytestorm.insightflow.domain.entity.Reuniao;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {
}
