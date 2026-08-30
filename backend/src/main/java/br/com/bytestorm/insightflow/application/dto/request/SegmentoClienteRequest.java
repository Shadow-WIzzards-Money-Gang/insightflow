package br.com.bytestorm.insightflow.application.dto.request;

import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import jakarta.validation.constraints.NotBlank;

public record SegmentoClienteRequest(

    @NotBlank(message = "O nome do segmento de cliente não pode ser nulo.") String nome

) {

    public static SegmentoClienteRequest fromEntity(SegmentoCliente segmentoCliente) {
        return new SegmentoClienteRequest(segmentoCliente.getNome());
    }

    public SegmentoCliente toEntity() {
        return SegmentoCliente.builder()
                .nome(this.nome)
                .build();
    }

}
