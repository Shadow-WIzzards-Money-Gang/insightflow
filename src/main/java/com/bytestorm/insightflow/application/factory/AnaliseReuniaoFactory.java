package com.bytestorm.insightflow.application.factory;

import com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import com.bytestorm.insightflow.domain.entity.Reuniao;

public class AnaliseReuniaoFactory {
    public static AnaliseReuniao criarAnalise(Reuniao reuniao) {
        return new AnaliseReuniao(reuniao);
    }
}
