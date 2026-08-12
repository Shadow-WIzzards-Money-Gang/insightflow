package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.request.ReuniaoRequest;
import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente.SegmentoNaoEncontrado;
import br.com.bytestorm.insightflow.infra.repository.ReuniaoRepository;
import br.com.bytestorm.insightflow.infra.repository.SegmentoClienteRepository;

@Service
public class ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;
    private final SegmentoClienteRepository segmentoClienteRepository;

    public ReuniaoService(ReuniaoRepository reuniaoRepository, SegmentoClienteRepository segmentoClienteRepository) {
        this.reuniaoRepository = reuniaoRepository;
        this.segmentoClienteRepository = segmentoClienteRepository;
    }

    public void criarReuniao(ReuniaoRequest request) {

        SegmentoCliente segmentoCliente = encontrarSegmentoCliente(request.segmentoClienteId());

        reuniaoRepository.save(request.toEntity(segmentoCliente));
    }

    public List<ReuniaoResponse> buscarTodos() {
        return this.reuniaoRepository.findAll().stream()
            .map((r) -> ReuniaoResponse.fromEntity(r))
            .toList();
    }

    private SegmentoCliente encontrarSegmentoCliente(Long id) {
        return this.segmentoClienteRepository.findById(id).orElseThrow(
            () -> new SegmentoNaoEncontrado()
        );
    }

}
