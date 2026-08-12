package br.com.bytestorm.insightflow.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;

public interface SegmentoClienteRepository extends JpaRepository<SegmentoCliente, Long> {

    Boolean existsByNomeIgnoreCase(String nome);

}
