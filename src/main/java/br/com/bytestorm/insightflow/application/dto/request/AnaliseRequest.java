package br.com.bytestorm.insightflow.application.dto.request;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnaliseRequest(

    @NotBlank(message = "A transcricao nao pode estar vazia!") String transcricaoBruta,
    @NotNull(message = "A data da reuniao nao pode estar vazia!") LocalDateTime dataReuniao,
    @NotNull(message = "A duracao nao pode estar vazia!") LocalTime duracao,
    @NotNull(message = "O segmento do cliente nao pode estar vazio!") Long segmentoClienteId    
) {
    
    public Reuniao toEntity(SegmentoCliente segmentoCliente, String hashTranscricao) {
        return Reuniao.builder()
                .transcricaoBruta(this.transcricaoBruta)
                .hashTranscricao(hashTranscricao)
                .dataReuniao(this.dataReuniao)
                .duracao(this.duracao)
                .segmentoCliente(segmentoCliente)
                .build();
    }

}
