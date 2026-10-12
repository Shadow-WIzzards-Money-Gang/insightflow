package br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs;
import br.com.bytestorm.insightflow.domain.exceptions.erro.NaoEncontradoException;

public class ProdutoNaoEncontradoException extends NaoEncontradoException {
    public ProdutoNaoEncontradoException() {
        super("Produto não encontrado.");
    }
}
