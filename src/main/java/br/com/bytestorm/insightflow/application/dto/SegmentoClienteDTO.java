package br.com.bytestorm.insightflow.application.dto;

import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import jakarta.validation.constraints.NotBlank;

public record SegmentoClienteDTO(

    @NotBlank(message = "O nome do segmento de cliente não pode ser nulo.") String nome

) {

    public static SegmentoClienteDTO fromEntity(SegmentoCliente segmentoCliente) {
        return new SegmentoClienteDTO(segmentoCliente.getNome());
    }

    public SegmentoCliente toEntity() {
        return SegmentoCliente.builder()
                .nome(this.nome)
                .build();
    }

}
