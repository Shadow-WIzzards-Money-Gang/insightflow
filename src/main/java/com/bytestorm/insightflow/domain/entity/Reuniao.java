package com.bytestorm.insightflow.domain.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import com.bytestorm.insightflow.application.service.AnalisarReuniaoService;
import com.bytestorm.insightflow.domain.valueobject.AnaliseReuniaoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Reuniao extends BaseEntity {
    private String transcricao;
    private Duration duracao;
    private LocalDateTime ocorreuEm;
    private GerenteVendas uploadedBy;

    private AnaliseReuniao analise;

    public Reuniao(String transcricao, Duration duracao, LocalDateTime ocorreuEm, GerenteVendas uploadedBy) {
        super();

        this.transcricao = transcricao;
        this.duracao = duracao;
        this.ocorreuEm = ocorreuEm;
        this.uploadedBy = uploadedBy;

        try {
            AnaliseReuniaoDTO analiseDTO = AnalisarReuniaoService.analisarTranscricao(transcricao, (int) duracao.toMinutes());
            this.analise = new AnaliseReuniao(analiseDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public String getTranscricao() {
        return transcricao;
    }

    public Duration getDuracao() {
        return duracao;
    }

    public LocalDateTime getOcorreuEm() {
        return ocorreuEm;
    }

    public GerenteVendas getUploadedBy() {
        return uploadedBy;
    }

    public AnaliseReuniao getAnalise() {
        return analise;
    }

    public static void main(String[] args) {

        String transcricao = "Reunião de alinhamento técnico e comercial entre o cliente (setor hospitalar) e a equipe da TOTVS sobre a migração/implantação do sistema de ponto eletrônico e gestão de escalas.\n\n" +
            "Principais pontos e requisitos discutidos:\n" +
            "1. Contexto e Segurança: O cliente possui um sistema interno próprio há anos e migrações anteriores geraram atritos internos. Há uma grande preocupação com a transição da equipe comercial para a equipe técnica de implantação.\n" +
            "2. Gestão de Escalas (Módulo de Escalas): Item crítico. O cliente atua no setor hospitalar (altamente complexo, com trocas de plantões e dobras). Eles precisam prever a escala do próximo período (mês futuro) para calcular e comprar antecipadamente benefícios como Vale Transporte e Vale Alimentação via Excel. O sistema precisa permitir que o gestor defina se as horas de 'dobra' serão pagas como extra ou compensadas.\n" +
            "3. Descentralização: O cliente já pratica a gestão descentralizada, validando o modelo do sistema onde os gestores de equipe são os 'olhos do RH' e operam diretamente na ponta.\n" +
            "4. Controle de Dois Saldos de Banco de Horas (Dificuldade Crítica): O cliente adquiriu um hospital há 3 anos que possuía um passivo de banco de horas eterno (antigo). Eles controlam hoje no Excel o banco antigo (que não zera fácil e tem regras de pagamento específicas em rescisões) e o banco oficial pós-compra (que fecha a cada 12 meses) via Chronus. O novo sistema precisará tratar essa separação de saldos (um conta corrente/tabela temporária) para evitar retrabalho, pois juridicamente apenas o banco oficial é exposto no espelho de ponto.\n" +
            "5. Estrutura Organizacional: O grupo econômico possui 12 coligadas/unidades com CNPJs distintos, totalizando cerca de 10.000 colaboradores. O sistema deve suportar a gestão apartada por filtros, relatórios hierárquicos e integração de dados salariais para alimentar painéis analíticos de custo em tempo real.\n" +
            "6. Sindicatos e Regras de Negócio: O cliente possui 32 sindicatos cadastrados. Reclamam que no sistema RM a parametrização por sindicato fica oculta e complexa. No novo sistema, esperam alertas de infração de leis ou regras sindicais em tempo real ao alterar escalas/jornadas.\n" +
            "7. Integração e e-Social: O sistema deve integrar-se totalmente com a Folha de Pagamento (RM/SESMIT) para envio de afastamentos, atestados e total de horas. Discutiu-se o problema atual de falta de rastreabilidade (gatilhos) quando gestores alteram horários de colaboradores sem o RH acompanhar, gerando problemas contratuais e na geração correta de eventos para o e-Social.";
            Reuniao reuniao = new Reuniao(
                transcricao,
                Duration.ofMinutes(45),
                LocalDateTime.now(),
                new GerenteVendas("João Silva", "joao.silva@exemplo.com", "123456789")
            );

        System.out.println("Assunto: %s".formatted(reuniao.getAnalise().getAssunto()));
        System.out.println("Sentimento: %s".formatted(reuniao.getAnalise().getSentimentoReuniao()));
        System.out.println("Risco de Cancelamento: %s".formatted(reuniao.getAnalise().getRiscoCancelamento()));
        System.out.println("Segmento do Cliente: %s".formatted(reuniao.getAnalise().getSegmentoCliente()));

    }
}
