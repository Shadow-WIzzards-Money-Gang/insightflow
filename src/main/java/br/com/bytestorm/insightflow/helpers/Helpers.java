package br.com.bytestorm.insightflow.helpers;

import java.nio.charset.StandardCharsets;

import org.springframework.util.DigestUtils;

import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
import br.com.bytestorm.insightflow.application.dto.response.ProdutoTotvsResponse;
import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.application.dto.response.SegmentoClienteResponse;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;

public class Helpers {

    public static String resumo(String text) {
        if (text != null && text.length() > 30) {
            return text.substring(0, 30) + "...";
        }
        return text;
    }

    public static ReuniaoResponse resumirReuniao(Reuniao reuniao) {
        return new ReuniaoResponse(
            reuniao.getId(),
            resumo(reuniao.getTranscricaoBruta()),
            reuniao.getDataReuniao(),
            reuniao.getDuracao(),
            SegmentoClienteResponse.fromEntity(reuniao.getSegmentoCliente()),
            reuniao.getCreatedAt()
        );
    }

    public static AnaliseResponse resumirAnalise(AnaliseReuniao analiseReuniao) {
        return new AnaliseResponse(
            analiseReuniao.getId(),
            analiseReuniao.getAssunto(),
            ProdutoTotvsResponse.fromEntity(analiseReuniao.getProdutoTotvs()),
            analiseReuniao.getSentimentoReuniao(),
            analiseReuniao.getRiscoCancelamento(),
            resumirReuniao(analiseReuniao.getReuniao())
        );
    }

    public static String gerarHash(String text) {
        String hashText = DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8));
        return hashText;
    }
}
