package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.helpers.Helpers;
import br.com.bytestorm.insightflow.infra.repository.ReuniaoRepository;

@Service
public class ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;
    private final SegmentoClienteService segmentoClienteService;

    public ReuniaoService(ReuniaoRepository reuniaoRepository, SegmentoClienteService segmentoClienteService) {
        this.reuniaoRepository = reuniaoRepository;
        this.segmentoClienteService = segmentoClienteService;
    }

    public Reuniao cadastrarReuniao(AnaliseRequest request) {
        SegmentoCliente segmentoCliente = segmentoClienteService.buscarPorId(request.segmentoClienteId());

        Reuniao reuniao = request.toEntity(segmentoCliente);

        return this.reuniaoRepository.save(reuniao);
    }

    public List<ReuniaoResponse> buscarReunioes() {
        return this.reuniaoRepository.findAll().stream()
                .map((r) -> Helpers.resumirReuniao(r))
                .toList();
    }

}
