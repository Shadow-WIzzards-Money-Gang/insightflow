package br.com.bytestorm.insightflow.application.service;

import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bytestorm.insightflow.application.dto.ia.AnaliseIAResult;
import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.helpers.Helpers;
import br.com.bytestorm.insightflow.infra.repository.AnaliseReuniaoRepository;
import br.com.bytestorm.insightflow.infra.repository.ProdutoTotvsRepository;

@Service
public class AnaliseService {   

    private static final String SYSTEM_INSTRUCTION = """
            Voce e um analista especializado em reunioes comerciais e de suporte da TOTVS.
            Voce vai receber a transcricao bruta de uma reuniao com um cliente e deve extrair:

            - assunto: um resumo curto (uma frase) do principal assunto tratado na reuniao.
            - sentimentoReuniao: o sentimento geral do cliente na reuniao. (POSITIVO, NEUTRO, NEGATIVO)
            - riscoCancelamento: o risco de cancelamento/churn do cliente, percebido a partir da conversa. (MUITO_ALTO, ALTO, MODERADO, BAIXO)
            - produtoTotvsNome: o produto TOTVS principal identificado na conversa. (APENAS UM)

            O produtoTotvsNome deve ser exatamente um dos seguintes nomes cadastrados (APENAS UM), sem alteracoes: %s
            Se nao houver um produto Totvs claramente identificado, retorne 'OUTROS'.

            Baseie-se apenas no conteudo da transcricao. Nao invente informacoes que nao estejam presentes nela.
            
            Retorne um objeto JSON com os campos: "assunto", "sentimentoReuniao", "riscoCancelamento" e "produtoTotvsNome"
            """;

    private final ChatClient chatClient;
    private final ReuniaoService reuniaoService;
    private final ProdutoTotvsRepository produtoTotvsRepository;
    private final ProdutoTotvsService produtoTotvsService;
    private final AnaliseReuniaoRepository analiseReuniaoRepository;

    public AnaliseService(ChatClient.Builder chatClientBuilder, ReuniaoService reuniaoService,
            ProdutoTotvsRepository produtoTotvsRepository, ProdutoTotvsService produtoTotvsService,
            AnaliseReuniaoRepository analiseReuniaoRepository) {
        this.chatClient = chatClientBuilder.build();
        this.reuniaoService = reuniaoService;
        this.produtoTotvsRepository = produtoTotvsRepository;
        this.produtoTotvsService = produtoTotvsService;
        this.analiseReuniaoRepository = analiseReuniaoRepository;
    }

    @Transactional
    public AnaliseResponse analisarReuniao(AnaliseRequest request) {

        Reuniao reuniao = reuniaoService.cadastrarReuniao(request);

        AnaliseIAResult resultadoIA = analisarComIA(reuniao.getTranscricaoBruta());

        ProdutoTotvs produtoTotvs = produtoTotvsService.buscarPorNome(resultadoIA.produtoTotvsNome());

        AnaliseReuniao analiseReuniao = AnaliseReuniao.builder()
                .assunto(resultadoIA.assunto())
                .sentimentoReuniao(SentimentoReuniao.fromString(resultadoIA.sentimentoReuniao().toUpperCase()))
                .riscoCancelamento(RiscoCancelamento.fromString(resultadoIA.riscoCancelamento().toUpperCase()))
                .produtoTotvs(produtoTotvs)
                .reuniao(reuniao)
                .build();

        return AnaliseResponse.fromEntity(analiseReuniaoRepository.save(analiseReuniao));
    }

    private AnaliseIAResult analisarComIA(String transcricaoBruta) {
        String produtosDisponiveis = produtoTotvsRepository.findAll().stream()
                .map(ProdutoTotvs::getNome)
                .collect(Collectors.joining(", "));

        return chatClient.prompt()
                .system(SYSTEM_INSTRUCTION.formatted(produtosDisponiveis))
                .user(transcricaoBruta)
                .call()
                .entity(AnaliseIAResult.class);
    }

    public Page<AnaliseResponse> buscarAnalises(Pageable pageable) {
        return this.analiseReuniaoRepository.findAll(pageable)
            .map((a) -> Helpers.resumirAnalise(a));
    }
}

