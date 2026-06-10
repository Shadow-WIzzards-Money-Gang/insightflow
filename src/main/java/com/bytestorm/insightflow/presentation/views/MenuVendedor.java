package com.bytestorm.insightflow.presentation.views;

import java.util.Scanner;

import com.bytestorm.insightflow.domain.entity.Vendedor;

public class MenuVendedor {

    private Scanner sc;
    private Vendedor vendedor;

    public MenuVendedor(Scanner sc, Vendedor vendedor) {
        this.sc = sc;
        this.vendedor = vendedor;
    }

    public void showMenu() {
        System.out.println("----- MENU VENDEDOR -----");
    }

}
