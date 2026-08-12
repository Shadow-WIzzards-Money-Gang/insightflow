package br.com.bytestorm.insightflow.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bytestorm.insightflow.application.dto.ia.AnaliseIAResult;
import br.com.bytestorm.insightflow.application.dto.request.AnaliseRequest;
import br.com.bytestorm.insightflow.application.dto.response.AnaliseResponse;
import br.com.bytestorm.insightflow.application.dto.response.ReuniaoResponse;
import br.com.bytestorm.insightflow.application.dto.response.SegmentoClienteResponse;
import br.com.bytestorm.insightflow.domain.entity.AnaliseReuniao;
import br.com.bytestorm.insightflow.domain.entity.ProdutoTotvs;
import br.com.bytestorm.insightflow.domain.entity.Reuniao;
import br.com.bytestorm.insightflow.domain.entity.SegmentoCliente;
import br.com.bytestorm.insightflow.domain.enums.RiscoCancelamento;
import br.com.bytestorm.insightflow.domain.enums.SentimentoReuniao;
import br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs.ProdutoNaoEncontradoException;
import br.com.bytestorm.insightflow.domain.exceptions.segmentoCliente.SegmentoNaoEncontradoException;
import br.com.bytestorm.insightflow.infra.repository.AnaliseReuniaoRepository;
import br.com.bytestorm.insightflow.infra.repository.ProdutoTotvsRepository;
import br.com.bytestorm.insightflow.infra.repository.ReuniaoRepository;
import br.com.bytestorm.insightflow.infra.repository.SegmentoClienteRepository;

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
    private final ReuniaoRepository reuniaoRepository;
    private final SegmentoClienteRepository segmentoClienteRepository;
    private final ProdutoTotvsRepository produtoTotvsRepository;
    private final AnaliseReuniaoRepository analiseReuniaoRepository;

    public AnaliseService(ChatClient.Builder chatClientBuilder, ReuniaoRepository reuniaoRepository,
            SegmentoClienteRepository segmentoClienteRepository, ProdutoTotvsRepository produtoTotvsRepository,
            AnaliseReuniaoRepository analiseReuniaoRepository) {
        this.chatClient = chatClientBuilder.build();
        this.reuniaoRepository = reuniaoRepository;
        this.segmentoClienteRepository = segmentoClienteRepository;
        this.produtoTotvsRepository = produtoTotvsRepository;
        this.analiseReuniaoRepository = analiseReuniaoRepository;
    }

    @Transactional
    public AnaliseResponse analisarReuniao(AnaliseRequest request) {

        Reuniao reuniao = cadastrarReuniao(request);

        AnaliseIAResult resultadoIA = analisarComIA(reuniao.getTranscricaoBruta());

        ProdutoTotvs produtoTotvs = produtoTotvsRepository.findByNomeIgnoreCase(resultadoIA.produtoTotvsNome())
                .orElseThrow(() -> new ProdutoNaoEncontradoException());

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

    private Reuniao cadastrarReuniao(AnaliseRequest request) {
        SegmentoCliente segmentoCliente = encontrarSegmentoCliente(request.segmentoClienteId());

        Reuniao reuniao = request.toEntity(segmentoCliente);

        return reuniaoRepository.save(reuniao);
    }

    public List<ReuniaoResponse> buscarReunioes() {
        return this.reuniaoRepository.findAll().stream()
            .map((r) -> paraResumo(r))
            .toList();
    }

    private ReuniaoResponse paraResumo(Reuniao reuniao) {
        String transcricaoBruta = reuniao.getTranscricaoBruta();
        String resumo = transcricaoBruta != null && transcricaoBruta.length() > 30
                ? transcricaoBruta.substring(0, 30) + "..."
                : transcricaoBruta;

        return new ReuniaoResponse(
                reuniao.getId(),
                resumo,
                reuniao.getDataReuniao(),
                reuniao.getDuracao(),
                SegmentoClienteResponse.fromEntity(reuniao.getSegmentoCliente()),
                reuniao.getCreatedAt()
        );
    }

    private SegmentoCliente encontrarSegmentoCliente(Long id) {
        return this.segmentoClienteRepository.findById(id).orElseThrow(
            () -> new SegmentoNaoEncontradoException()
        );
    }

}
