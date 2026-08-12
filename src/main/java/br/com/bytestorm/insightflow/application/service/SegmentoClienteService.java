package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.request.SegmentoClienteRequest;
import br.com.bytestorm.insightflow.application.dto.response.SegmentoClienteResponse;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente.SegmentoJaCadastradoException;
import br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente.SegmentoNaoEncontradoException;
import br.com.bytestorm.insightflow.infra.repository.SegmentoClienteRepository;

@Service
public class SegmentoClienteService {

    private final SegmentoClienteRepository segmentoClienteRepository;

    public SegmentoClienteService(SegmentoClienteRepository segmentoClienteRepository) {
        this.segmentoClienteRepository = segmentoClienteRepository;
    }

    public void cadastrarSegmentoCliente(SegmentoClienteRequest request) {

        if (segmentoClienteRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new SegmentoJaCadastradoException();
        }

        segmentoClienteRepository.save(request.toEntity());
    }

    public List<SegmentoClienteResponse> buscarTodos() {
        return segmentoClienteRepository.findAll().stream()
                .map((s) -> SegmentoClienteResponse.fromEntity(s))
                .toList();
    }

    public void deletarSegmentoCliente(Long id) {
        SegmentoCliente segmento = buscaPorId(id);
        segmentoClienteRepository.delete(segmento);
    }

    public SegmentoCliente buscaPorId(Long id) {
        return segmentoClienteRepository.findById(id).orElseThrow(
            () -> new SegmentoNaoEncontradoException()
        );
    }

}
