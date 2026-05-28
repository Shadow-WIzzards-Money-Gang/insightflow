package com.bytestorm.insightflow.utils;

public enum Cor {

    RESET("\u001B[0m"),
    VERMELHO("\u001B[31m"),
    VERDE("\u001B[32m"),
    AMARELO("\u001B[33m"),
    AZUL("\u001B[34m"),
    MAGENTA("\u001B[35m"),
    CIANO("\u001B[36m"),
    BRANCO("\u001B[37m");

    private final String codigo;

    Cor(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
