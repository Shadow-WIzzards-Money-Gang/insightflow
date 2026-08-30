package br.com.bytestorm.insightflow.application.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.bytestorm.insightflow.domain.entity.Reuniao;

public record ReuniaoResponse(

    Long id,
    String transcricaoBruta,
    LocalDateTime dataReuniao,
    LocalTime duracao,
    SegmentoClienteResponse segmentoCliente,
    LocalDateTime createdAt

) {

    public static ReuniaoResponse fromEntity(Reuniao reuniao) {
        return new ReuniaoResponse(
            reuniao.getId(),
            reuniao.getTranscricaoBruta(),
            reuniao.getDataReuniao(),
            reuniao.getDuracao(),
            SegmentoClienteResponse.fromEntity(reuniao.getSegmentoCliente()),
            reuniao.getCreatedAt()
        );
    }

}
