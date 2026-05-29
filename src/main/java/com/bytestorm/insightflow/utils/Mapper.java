package com.bytestorm.insightflow.utils;

import com.bytestorm.insightflow.application.dto.AnaliseReuniaoDTO;
import com.bytestorm.insightflow.domain.exceptions.analise.FalhaAnaliseTranscricaoException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {
    public static AnaliseReuniaoDTO parseAnalise(String texto) {
        ObjectMapper mapper = new ObjectMapper();

        AnaliseReuniaoDTO analise;
        try {
            analise = mapper.readValue(
                texto,
                AnaliseReuniaoDTO.class
            );
        } catch (JsonProcessingException e) {
            throw new FalhaAnaliseTranscricaoException();
        }
        return analise;
    }
}
