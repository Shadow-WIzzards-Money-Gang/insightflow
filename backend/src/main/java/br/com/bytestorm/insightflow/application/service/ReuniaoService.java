package br.com.bytestorm.insightflow.application.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.application.mapper.PaginaMapper;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.domain.exceptions.reuniao.ReuniaoNaoEncontradaException;
import br.com.bytestorm.insightflow.domain.repository.ReuniaoRepository;
import br.com.bytestorm.insightflow.domain.vo.HashTranscricao;

@Service
public class ReuniaoService {

    private static final Logger log = LoggerFactory.getLogger(ReuniaoService.class);

    private final ReuniaoRepository reuniaoRepository;
    private final SegmentoClienteService segmentoClienteService;

    public ReuniaoService(ReuniaoRepository reuniaoRepository, SegmentoClienteService segmentoClienteService) {
        this.reuniaoRepository = reuniaoRepository;
        this.segmentoClienteService = segmentoClienteService;
    }

    public Reuniao cadastrarReuniao(AnaliseRequest request) {
        SegmentoCliente segmentoCliente = segmentoClienteService.buscarPorId(request.segmentoClienteId());
        Reuniao reuniao = request.toEntity(segmentoCliente);

        log.info("Consultando banco de dados - salvando reunião");
        Reuniao reuniaoSalva = this.reuniaoRepository.salvar(reuniao);
        log.info("Consulta finalizada - reunião id={} salva", reuniaoSalva.getId());

        return reuniaoSalva;
    }

    public Optional<Reuniao> buscarPorHashTranscricao(HashTranscricao hashTranscricao) {
        log.info("Consultando banco de dados - buscando reunião por hash de transcrição");
        Optional<Reuniao> reuniao = this.reuniaoRepository.buscarPorHashTranscricao(hashTranscricao);
        log.info("Consulta finalizada - reunião existente: {}", reuniao.isPresent());

        return reuniao;
    }

    public Page<ReuniaoResponse> buscarReunioes(Pageable pageable) {
        log.info("Consultando banco de dados - buscando reuniões (page={}, size={})", pageable.getPageNumber(), pageable.getPageSize());
        Page<ReuniaoResponse> reunioes = PaginaMapper.toPage(
            this.reuniaoRepository.buscarTodas(PaginaMapper.toPaginacao(pageable))
                .mapear((r) -> ReuniaoResponse.resumida(r)),
            pageable
        );
        log.info("Consulta finalizada - {} reuniões encontradas", reunioes.getTotalElements());

        return reunioes;
    }

    public ReuniaoResponse buscarReuniaoPorId(Long id) {
        log.info("Consultando banco de dados - buscando reunião id={}", id);
        ReuniaoResponse reuniao = this.reuniaoRepository.buscarPorId(id)
            .map(r -> ReuniaoResponse.fromEntity(r))
            .orElseThrow(() -> new ReuniaoNaoEncontradaException());
        log.info("Consulta finalizada - reunião id={} encontrada", id);

        return reuniao;
    }

}
