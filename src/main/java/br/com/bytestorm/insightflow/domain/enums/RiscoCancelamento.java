package br.com.bytestorm.insightflow.domain.enums;

public enum RiscoCancelamento {

    MUITO_ALTO,
    ALTO,
    MODERADO,
    BAIXO;

    public static RiscoCancelamento fromString(String s) {
        for (RiscoCancelamento risco : RiscoCancelamento.values()) {
            if (risco.name().equalsIgnoreCase(s)) {
                return risco;
            }
        }
        return BAIXO;
    }

}
