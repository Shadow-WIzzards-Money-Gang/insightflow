package br.com.bytestorm.insightflow.domain.gateway;

import br.com.bytestorm.insightflow.domain.vo.ResultadoAnaliseIA;

/** Analise de uma transcricao por IA. Quem implementa devolve um resultado ja validado. */
public interface AnalisadorReuniaoGateway {
    ResultadoAnaliseIA analisar(String transcricao, String produtosDisponiveis);
}
