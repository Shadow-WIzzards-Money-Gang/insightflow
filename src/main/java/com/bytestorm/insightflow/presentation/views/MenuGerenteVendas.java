package com.bytestorm.insightflow.presentation.views;

import java.util.Scanner;

import com.bytestorm.insightflow.domain.entity.GerenteVendas;

public class MenuGerenteVendas {

    private Scanner sc;
    private GerenteVendas gerenteVendas;

    public MenuGerenteVendas(Scanner sc, GerenteVendas gerenteVendas) {
        this.sc = sc;
        this.gerenteVendas = gerenteVendas;
    }

    public void showMenu() {
        System.out.println("----- MENU GERENTE DE VENDAS -----");
    }
}
