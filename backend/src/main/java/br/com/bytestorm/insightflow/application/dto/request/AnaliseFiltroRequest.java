package br.com.bytestorm.insightflow.application.dto.request;

import java.util.List;

public record AnaliseFiltroRequest(
    List<String> produtos,
    List<String> segmentos,
    List<String> sentimentos,
    List<String> riscos
) {
    
}
