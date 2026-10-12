package br.com.bytestorm.insightflow.application.dto.request;

import java.util.List;

import br.com.bytestorm.insightflow.domain.vo.FiltroAnalise;

public record AnaliseFiltroRequest(
    List<String> produtos,
    List<String> segmentos,
    List<String> sentimentos,
    List<String> riscos
) {

    public FiltroAnalise toFiltro() {
        return FiltroAnalise.de(this.produtos, this.segmentos, this.sentimentos, this.riscos);
    }

}
