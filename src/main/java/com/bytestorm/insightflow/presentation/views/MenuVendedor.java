package com.bytestorm.insightflow.presentation.views;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.bytestorm.insightflow.application.service.ReuniaoService;
import com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import com.bytestorm.insightflow.domain.entity.Reuniao;
import com.bytestorm.insightflow.domain.exceptions.menu.OpcaoInvalidaException;
import com.bytestorm.insightflow.domain.entity.Vendedor;
import com.bytestorm.insightflow.utils.Cor;

public class MenuVendedor {

    private Scanner sc;
    private ReuniaoService reuniaoService = ReuniaoService.getInstance();
    private Vendedor vendedor;

    private Boolean rodando = true;

    public MenuVendedor(Scanner sc, Vendedor vendedor) {
        this.sc = sc;
        this.vendedor = vendedor;
    }

    public void showMenu() {
        while (this.rodando) {
            System.out.println("----- MENU VENDEDOR - Bem vindo, " + this.vendedor.getNome() + " -----");

            System.out.println("1. Visualizar reuniões");
            System.out.println("2. Visualizar análises");
            System.out.println("3. Visualizar métricas");
            System.out.println("0. Sair");

            System.out.print(">>> ");
            
            try {
                String opcao = sc.nextLine();
                this.gerenciarOpcao(opcao);
            } catch (InputMismatchException ex) {
                System.out.println(new OpcaoInvalidaException().getMessage());
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void gerenciarOpcao(String opcao) {
        switch (opcao) {
            case "1":
                this.visualizarReunioes();
                break;
            case "2":
                this.visualizarAnalises();
                break;
            case "3":
                this.visualizarMetricas();
                break;
            case "0":
                this.sair();
                break;
            default:
                System.out.println(new OpcaoInvalidaException().getMessage());
                break;
        }
    }

    private void visualizarReunioes() {
        System.out.println("----- Visualizar reuniões -----");
        List<Reuniao> reunioes = this.reuniaoService.getReunioes();

        if (reunioes.size() == 0) {
            System.out.println(Cor.AMARELO.getCodigo() + "Nenhuma reunião encontrada." + Cor.RESET.getCodigo());
            this.continuar();
            return;
        }

        for (Reuniao reuniao : reunioes) {
            System.out.println(reuniao);
        }

        this.continuar();
    }

    private void visualizarAnalises() {
        System.out.println("----- Visualizar análises -----");
        List<AnaliseReuniao> analises = this.reuniaoService.getAnalises();

        if (analises.size() == 0) {
            System.out.println(Cor.AMARELO.getCodigo() + "Nenhuma análise encontrada." + Cor.RESET.getCodigo());
            this.continuar();
            return;
        }

        for (AnaliseReuniao analise : analises) {
            System.out.println(analise);
        }

        this.continuar();
    }

    private void visualizarMetricas() {
        System.out.println("----- Visualizar métricas -----");
        Map<String, String> metricas = this.reuniaoService.getMetricas();

        if (metricas.size() == 0) {
            System.out.println(Cor.AMARELO.getCodigo() + "Nenhuma análise registrada." + Cor.RESET.getCodigo());
            this.continuar();
            return;
        }

        for (Map.Entry<String, String> entry : metricas.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        this.continuar();
    }

    private void sair() {
        System.out.println(Cor.AMARELO.getCodigo() + "Saindo..." + Cor.RESET.getCodigo());
        this.rodando = false;
    }

    private void continuar() {
        System.out.print(Cor.AZUL.getCodigo() + "\nPressione Enter para continuar..." + Cor.RESET.getCodigo());
        this.sc.nextLine();
    }

}
