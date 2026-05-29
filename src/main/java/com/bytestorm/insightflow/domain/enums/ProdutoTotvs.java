package com.bytestorm.insightflow.domain.enums;

public enum ProdutoTotvs {

    // ERPs
    PROTHEUS("Protheus", "ERP"),
    RM("RM", "ERP"),
    DATASUL("Datasul", "ERP"),
    LOGIX("Logix", "ERP"),

    // RH
    RH_CLOCKIN("RH Clock In", "RH"),
    RH_PERFORMANCE("RH Performance", "RH"),

    // CRM e Vendas
    RD_STATION("RD Station", "Marketing e CRM"),

    // Assinatura eletrônica
    TOTVS_ASSINATURA("TOTVS Assinatura Eletronica", "Assinatura"),

    // Analytics
    TOTVS_ANALYTICS("TOTVS Analytics", "BI e Analytics"),

    // Automação / Fluig
    FLUIG("Fluig", "Workflow e BPM"),

    // Fiscal / Financeiro
    TOTVS_AUTOMACAO_FISCAL("TOTVS Automacao Fiscal", "Fiscal"),

    // Educação
    EDUCACAO("TOTVS Educacional", "Educação"),

    // Não identificado
    NAO_IDENTIFICADO("Não Identificado", "Não Identificado");

    private final String descricao;
    private final String categoria;

    ProdutoTotvs(String descricao, String categoria) {
        this.descricao = descricao;
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public static ProdutoTotvs fromDescricao(String descricao) {
        for (ProdutoTotvs produto : ProdutoTotvs.values()) {
            if (produto.getDescricao().equalsIgnoreCase(descricao.replace(" ", "_"))) {
                return produto;
            }
        }
        return null;
    }
}