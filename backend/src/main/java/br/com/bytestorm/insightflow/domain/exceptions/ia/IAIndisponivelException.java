package br.com.bytestorm.insightflow.domain.exceptions.ia;

import br.com.bytestorm.insightflow.domain.exceptions.erro.ServicoIndisponivelException;

/**
 * O provedor de IA nao respondeu ou falhou de forma transitoria
 * (timeout, indisponibilidade, limite de requisicoes, erro 5xx do provedor).
 * O cliente pode tentar novamente mais tarde.
 */
public class IAIndisponivelException extends ServicoIndisponivelException {

    public IAIndisponivelException(Throwable cause) {
        super("O servico de analise por IA esta indisponivel no momento. Tente novamente em instantes.", cause);
    }
}
