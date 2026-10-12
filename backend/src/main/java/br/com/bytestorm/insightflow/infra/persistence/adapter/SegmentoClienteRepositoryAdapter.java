package br.com.bytestorm.insightflow.infra.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.domain.repository.SegmentoClienteRepository;
import br.com.bytestorm.insightflow.infra.persistence.mapper.SegmentoClienteMapper;
import br.com.bytestorm.insightflow.infra.persistence.repository.SegmentoClienteJpaRepository;

@Repository
@Transactional(readOnly = true)
public class SegmentoClienteRepositoryAdapter implements SegmentoClienteRepository {

    private final SegmentoClienteJpaRepository jpaRepository;

    public SegmentoClienteRepositoryAdapter(SegmentoClienteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public SegmentoCliente salvar(SegmentoCliente segmentoCliente) {
        return SegmentoClienteMapper.toDomain(jpaRepository.save(SegmentoClienteMapper.toJpa(segmentoCliente)));
    }

    @Override
    @Transactional
    public void remover(SegmentoCliente segmentoCliente) {
        jpaRepository.deleteById(segmentoCliente.getId());
    }

    @Override
    public List<SegmentoCliente> buscarTodos() {
        return jpaRepository.findAll().stream().map(SegmentoClienteMapper::toDomain).toList();
    }

    @Override
    public Optional<SegmentoCliente> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(SegmentoClienteMapper::toDomain);
    }

    @Override
    public Optional<SegmentoCliente> buscarPorNome(String nome) {
        return jpaRepository.findByNomeIgnoreCase(nome).map(SegmentoClienteMapper::toDomain);
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.existsByNomeIgnoreCase(nome);
    }
}
