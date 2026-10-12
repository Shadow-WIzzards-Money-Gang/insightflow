package br.com.bytestorm.insightflow.domain.repository;

import java.util.List;
import java.util.Optional;

import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;

public interface SegmentoClienteRepository {

    SegmentoCliente salvar(SegmentoCliente segmentoCliente);

    void remover(SegmentoCliente segmentoCliente);

    List<SegmentoCliente> buscarTodos();

    Optional<SegmentoCliente> buscarPorId(Long id);

    Optional<SegmentoCliente> buscarPorNome(String nome);

    boolean existePorNome(String nome);
}
