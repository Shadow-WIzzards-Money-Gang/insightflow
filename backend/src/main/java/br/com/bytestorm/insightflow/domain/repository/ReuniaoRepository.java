package br.com.bytestorm.insightflow.domain.repository;

import java.util.Optional;

import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.vo.HashTranscricao;
import br.com.bytestorm.insightflow.domain.vo.Pagina;
import br.com.bytestorm.insightflow.domain.vo.Paginacao;

public interface ReuniaoRepository {

    Reuniao salvar(Reuniao reuniao);

    Optional<Reuniao> buscarPorId(Long id);

    Optional<Reuniao> buscarPorHashTranscricao(HashTranscricao hashTranscricao);

    Pagina<Reuniao> buscarTodas(Paginacao paginacao);
}
