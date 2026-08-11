package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.SegmentoClienteDTO;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.infra.repository.SegmentoClienteRepository;

@Service
public class SegmentoClienteService {

    private final SegmentoClienteRepository segmentoClienteRepository;

    public SegmentoClienteService(SegmentoClienteRepository segmentoClienteRepository) {
        this.segmentoClienteRepository = segmentoClienteRepository;
    }

    public void cadastrarSegmentoCliente(SegmentoClienteDTO request) {
        segmentoClienteRepository.save(request.toEntity());
    }

    public List<SegmentoCliente> buscarTodos() {
        return segmentoClienteRepository.findAll();
    }

}
