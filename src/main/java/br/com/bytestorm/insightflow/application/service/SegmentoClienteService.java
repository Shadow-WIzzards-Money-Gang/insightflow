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

    public SegmentoClienteResponse buscarPorNome(String nome) {
        SegmentoCliente segmento = segmentoClienteRepository.findByNomeIgnoreCase(nome).orElseThrow(
            () -> new SegmentoNaoEncontradoException()
        );
        return SegmentoClienteResponse.fromEntity(segmento);
    }

    public void deletarSegmentoCliente(Long id) {
        SegmentoCliente segmento = buscarPorId(id);
        segmentoClienteRepository.delete(segmento);
    }

    public SegmentoCliente buscarPorId(Long id) {
        return segmentoClienteRepository.findById(id).orElseThrow(
            () -> new SegmentoNaoEncontradoException()
        );
    }

}
