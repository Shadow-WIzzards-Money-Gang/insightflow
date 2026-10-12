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

    private static final int TAMANHO_RESUMO = 30;

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

    /** Versao para listagem: a transcricao vem cortada nos primeiros caracteres. */
    public static ReuniaoResponse resumida(Reuniao reuniao) {
        return new ReuniaoResponse(
            reuniao.getId(),
            resumo(reuniao.getTranscricaoBruta()),
            reuniao.getDataReuniao(),
            reuniao.getDuracao(),
            SegmentoClienteResponse.fromEntity(reuniao.getSegmentoCliente()),
            reuniao.getCreatedAt()
        );
    }

    private static String resumo(String text) {
        if (text != null && text.length() > TAMANHO_RESUMO) {
            return text.substring(0, TAMANHO_RESUMO) + "...";
        }
        return text;
    }

}
