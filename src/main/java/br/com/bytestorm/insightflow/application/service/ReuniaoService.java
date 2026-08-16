package br.com.bytestorm.insightflow.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.domain.exceptions.reuniao.ReuniaoJaAnalisadaException;
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

        String hashTranscricao = Helpers.gerarHash(request.transcricaoBruta());

        if (reuniaoRepository.existsByHashTranscricao(hashTranscricao)) {
            throw new ReuniaoJaAnalisadaException();
        }

        SegmentoCliente segmentoCliente = segmentoClienteService.buscarPorId(request.segmentoClienteId());
        Reuniao reuniao = request.toEntity(segmentoCliente, hashTranscricao);

        return this.reuniaoRepository.save(reuniao);
    }

    public Page<ReuniaoResponse> buscarReunioes(Pageable pageable) {
        return this.reuniaoRepository.findAll(pageable)
                .map((r) -> Helpers.resumirReuniao(r));
    }

}
