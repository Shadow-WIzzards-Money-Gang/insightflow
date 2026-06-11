package com.bytestorm.insightflow.presentation;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.bytestorm.insightflow.application.service.AcessoColaboradorService;
import com.bytestorm.insightflow.domain.entity.Colaborador;
import com.bytestorm.insightflow.domain.entity.GerenteVendas;
import com.bytestorm.insightflow.domain.entity.Vendedor;
import com.bytestorm.insightflow.domain.enums.TipoColaborador;
import com.bytestorm.insightflow.domain.exceptions.menu.OpcaoInvalidaException;
import com.bytestorm.insightflow.presentation.views.MenuGerenteVendas;
import com.bytestorm.insightflow.presentation.views.MenuVendedor;
import com.bytestorm.insightflow.utils.Cor;

public class Menu {

    private static Menu instance;
    private Scanner sc = new Scanner(System.in);
    private AcessoColaboradorService acessoColaboradorService = AcessoColaboradorService.getInstance();

    public static Menu getInstance() {
        if (instance == null) {
            instance = new Menu();
        }
        return instance;
    }

    public void showMenu() {
        while (true) {
            System.out.println("----- INSIGHT FLOW -----");
            
            System.out.println("1. Cadastrar colaborador");
            System.out.println("2. Entrar");
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

    private void gerenciarOpcao(String opcao) {
        switch (opcao) {
            case "1":
                this.cadastrarColaborador();
                break;
            case "2":
                this.entrar();
                break;
            case "0":
                this.sair();
                break;
            default:
                System.out.println(new OpcaoInvalidaException().getMessage());
                break;
        }
    }

    private void cadastrarColaborador() {
        
        TipoColaborador tipo;
        String nome;
        String email;
        String senha;
        
        System.out.println("----- Cadastrar colaborador -----");

        while (true) {
            System.out.println("Informe o tipo do colaborador (digite V para voltar): ");
            System.out.println("1. Vendedor");
            System.out.println("2. Gerente de Vendas");
            System.out.print(">>> ");

            String opcao = sc.nextLine();
            
            if (opcao.equalsIgnoreCase("V")) {
                return;
            }
            
            if (opcao.equals("1")) {
                tipo = TipoColaborador.VENDEDOR;
                break;
            } else if (opcao.equals("2")) {
                tipo = TipoColaborador.GERENTE_VENDAS;
                break;
            } else {
                System.out.println(new OpcaoInvalidaException().getMessage());
            }
        }

        while (true) {
            System.out.print("Informe o nome do colaborador (digite V para voltar): ");
            nome = sc.nextLine();

            if (nome.equalsIgnoreCase("V")) {
                return;
            }

            try {
                acessoColaboradorService.nomeValido(nome);
                break;
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }

        while (true) {
            System.out.print("Informe o email do colaborador (digite V para voltar): ");
            email = sc.nextLine();

            if (email.equalsIgnoreCase("V")) {
                return;
            }

            try {
                acessoColaboradorService.emailValido(email);
                break;
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }

        while (true) {
            System.out.print("Informe a senha do colaborador (digite V para voltar): ");
            senha = sc.nextLine();

            if (senha.equalsIgnoreCase("V")) {
                return;
            }

            try {
                acessoColaboradorService.senhaValida(senha);
                break;
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }

        try {
            acessoColaboradorService.cadastrarColaborador(nome, email, senha, tipo);
            System.out.println(Cor.VERDE.getCodigo() + "Colaborador cadastrado com sucesso!" + Cor.RESET.getCodigo());
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void entrar() {
        System.out.println("----- Entrar -----");

        String email;
        String senha;

        Colaborador colaborador;

        while (true) {
            System.out.print("Informe o email do colaborador (digite V para voltar): ");
            email = sc.nextLine();

            if (email.equalsIgnoreCase("V")) {
                return;
            }

            try {
                colaborador = acessoColaboradorService.encontrarColaborador(email);
                break;
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }

        while (true) {
            System.out.print("Informe a senha do colaborador (digite V para voltar): ");
            senha = sc.nextLine();

            if (senha.equalsIgnoreCase("V")) {
                return;
            }

            try {
                acessoColaboradorService.autenticar(colaborador, senha);
                System.out.println(Cor.VERDE.getCodigo() + "Login realizado com sucesso!" + Cor.RESET.getCodigo());

                if (colaborador instanceof GerenteVendas) {
                    new MenuGerenteVendas(sc, (GerenteVendas) colaborador).showMenu();
                }

                if (colaborador instanceof Vendedor) {
                    new MenuVendedor(sc, (Vendedor) colaborador).showMenu();
                }

                break;
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void sair() {
        System.out.println(Cor.AMARELO.getCodigo() + "Saindo..." + Cor.RESET.getCodigo());
        System.exit(0);
    }

}
 