package com.bytestorm.insightflow.presentation.views;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.bytestorm.insightflow.application.service.GerenteVendasService;
import com.bytestorm.insightflow.application.service.ReuniaoService;
import com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import com.bytestorm.insightflow.domain.entity.Colaborador;
import com.bytestorm.insightflow.domain.entity.GerenteVendas;
import com.bytestorm.insightflow.domain.entity.Reuniao;
import com.bytestorm.insightflow.domain.exceptions.menu.OpcaoInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.reuniao.DataInvalidaException;
import com.bytestorm.insightflow.domain.exceptions.reuniao.DuracaoInvalidaException;
import com.bytestorm.insightflow.utils.Cor;

public class MenuGerenteVendas {

    private Scanner sc;
    private GerenteVendas gerenteVendas;
    private GerenteVendasService gerenteVendasService = GerenteVendasService.getInstance();
    private ReuniaoService reuniaoService = ReuniaoService.getInstance();

    private Boolean rodando = true;

    public MenuGerenteVendas(Scanner sc, GerenteVendas gerenteVendas) {
        this.sc = sc;
        this.gerenteVendas = gerenteVendas;
    }

    public void showMenu() {
        while (this.rodando) {
            System.out.println("----- MENU GERENTE DE VENDAS - Bem vindo, " + this.gerenteVendas.getNome() + " -----");

            System.out.println("1. Visualizar colaboradores");
            System.out.println("2. Analisar reunião");
            System.out.println("3. Visualizar reuniões");
            System.out.println("4. Visualizar análises");
            System.out.println("5. Visualizar métricas");
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
                this.visualizarColaboradores();
                break;
            case "2":
                this.cadastrarReuniao();
                break;
            case "3":
                this.visualizarReunioes();
                break;
            case "4":
                this.visualizarAnalises();
                break;
            case "5":
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

    private void visualizarColaboradores() {
        System.out.println("----- Visualizar colaboradores -----");
        List<Colaborador> colaboradores = this.gerenteVendasService.getColaboradores();

        for (Colaborador colaborador : colaboradores) {
            System.out.println(colaborador);
        }

        this.continuar();
    }

    private void cadastrarReuniao() {
        System.out.println("----- Cadastrar reunião -----");

        String transcricao;
        Duration duracao;
        LocalDate ocorreuEm;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            System.out.print("Digite a transcrição da reunião (digite V para voltar): ");
            transcricao = this.sc.nextLine();

            if (transcricao.equalsIgnoreCase("V")) {
                return;
            }

            try {
                this.gerenteVendasService.transcricaoValida(transcricao);
                break;
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }

        while (true) {
            System.out.print("Digite a duração da reunião em minutos (digite V para voltar): ");
            String duracaoStr = this.sc.nextLine();

            if (duracaoStr.equalsIgnoreCase("V")) {
                return;
            }

            try {
                duracao = Duration.ofMinutes(Long.parseLong(duracaoStr));
                this.gerenteVendasService.duracaoValida(duracao);
                break;
            } catch (NumberFormatException ex) {
                System.out.println(new DuracaoInvalidaException().getMessage());
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }

        while (true) {
            System.out.print("Digite a data da reunião (dd/mm/yyyy) (digite V para voltar): ");
            String dataStr = this.sc.nextLine();

            if (dataStr.equalsIgnoreCase("V")) {
                return;
            }

            try {
                ocorreuEm = LocalDate.parse(dataStr, formatter);
                this.gerenteVendasService.dataValida(ocorreuEm);
                break;
            } catch (DateTimeParseException ex) {
                System.out.println(new DataInvalidaException().getMessage());
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }

        try {
            this.gerenteVendasService.cadastrarReuniao(transcricao, duracao, ocorreuEm, this.gerenteVendas);
            System.out.println(Cor.VERDE.getCodigo() + "Reunião analisada com sucesso!" + Cor.RESET.getCodigo());
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
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
