package br.com.bytestorm.insightflow.application.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.bytestorm.insightflow.application.dto.request.SegmentoClienteRequest;
import br.com.bytestorm.insightflow.application.dto.response.SegmentoClienteResponse;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente.SegmentoJaCadastradoException;
import br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente.SegmentoNaoEncontradoException;
import br.com.bytestorm.insightflow.infra.repository.SegmentoClienteRepository;

@Service
public class SegmentoClienteService {

    private static final Logger log = LoggerFactory.getLogger(SegmentoClienteService.class);

    private final SegmentoClienteRepository segmentoClienteRepository;

    public SegmentoClienteService(SegmentoClienteRepository segmentoClienteRepository) {
        this.segmentoClienteRepository = segmentoClienteRepository;
    }

    public void cadastrarSegmentoCliente(SegmentoClienteRequest request) {
        log.info("Consultando banco de dados - verificando existência do segmento '{}'", request.nome());
        boolean jaExiste = segmentoClienteRepository.existsByNomeIgnoreCase(request.nome());
        log.info("Consulta finalizada - segmento '{}' já existe: {}", request.nome(), jaExiste);

        if (jaExiste) {
            throw new SegmentoJaCadastradoException();
        }

        log.info("Consultando banco de dados - salvando segmento '{}'", request.nome());
        segmentoClienteRepository.save(request.toEntity());
        log.info("Consulta finalizada - segmento '{}' salvo", request.nome());
    }

    public List<SegmentoClienteResponse> buscarTodos() {
        log.info("Consultando banco de dados - buscando todos os segmentos");
        List<SegmentoClienteResponse> segmentos = segmentoClienteRepository.findAll().stream()
                .map((s) -> SegmentoClienteResponse.fromEntity(s))
                .toList();
        log.info("Consulta finalizada - {} segmentos encontrados", segmentos.size());

        return segmentos;
    }

    public SegmentoClienteResponse buscarPorNome(String nome) {
        log.info("Consultando banco de dados - buscando segmento por nome '{}'", nome);
        SegmentoCliente segmento = segmentoClienteRepository.findByNomeIgnoreCase(nome).orElseThrow(
            () -> new SegmentoNaoEncontradoException()
        );
        log.info("Consulta finalizada - segmento '{}' encontrado", nome);

        return SegmentoClienteResponse.fromEntity(segmento);
    }

    public void deletarSegmentoCliente(Long id) {
        SegmentoCliente segmento = buscarPorId(id);

        log.info("Consultando banco de dados - excluindo segmento id={}", id);
        segmentoClienteRepository.delete(segmento);
        log.info("Consulta finalizada - segmento id={} excluído", id);
    }

    public SegmentoCliente buscarPorId(Long id) {
        log.info("Consultando banco de dados - buscando segmento id={}", id);
        SegmentoCliente segmento = segmentoClienteRepository.findById(id).orElseThrow(
            () -> new SegmentoNaoEncontradoException()
        );
        log.info("Consulta finalizada - segmento id={} encontrado", id);

        return segmento;
    }

}
