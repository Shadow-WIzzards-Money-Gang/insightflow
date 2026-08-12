package br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs;
import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro404Exception;

public class ProdutoNaoEncontradoException extends Erro404Exception {
    public ProdutoNaoEncontradoException() {
        super("Produto não encontrado.");
    }
}
