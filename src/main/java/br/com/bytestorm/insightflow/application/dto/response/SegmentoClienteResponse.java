package br.com.bytestorm.insightflow.application.dto.response;

import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;

public record SegmentoClienteResponse(

    Long id,
    String nome

) {

    public static SegmentoClienteResponse fromEntity(SegmentoCliente segmentoCliente) {
        return new SegmentoClienteResponse(segmentoCliente.getId(), segmentoCliente.getNome());
    }

}
