package com.bytestorm.insightflow.application.factory;

import java.time.Duration;
import java.time.LocalDate;

import com.bytestorm.insightflow.domain.entity.GerenteVendas;
import com.bytestorm.insightflow.domain.entity.Reuniao;

public class ReuniaoFactory {
    public static Reuniao criarReuniao(String transcricao, Duration duracao, LocalDate ocorreuEm, GerenteVendas uploadedBy) {
        return new Reuniao(transcricao, duracao, ocorreuEm, uploadedBy);
    }
}
