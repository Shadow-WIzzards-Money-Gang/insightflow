package com.bytestorm.insightflow.domain.enums;

public enum SegmentoCliente {
    SAUDE("Saude"),
    DISTRIBUICAO("Distribuicao"),
    MANUFATURA("Manufatura"),
    SERVICOS("Servicos"),
    CONSTRUCAO_E_PROJETOS("Construcao e Projetos"),
    EDUCACIONAL("Educacional"),
    AGROINDUSTRIA("Agroindustria"),
    LOGISTICA("Logistica"),
    SUPERMERCADOS("Supermercados"),
    VAREJO("Varejo"),
    NAO_IDENTIFICADO("Nao Identificado");

    private final String descricao;

    SegmentoCliente(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static SegmentoCliente fromDescricao(String descricao) {
        for (SegmentoCliente segmento : SegmentoCliente.values()) {
            if (segmento.getDescricao().equalsIgnoreCase(descricao.replace(" ", "_"))) {
                return segmento;
            }
        }
        return null;
    }
}
