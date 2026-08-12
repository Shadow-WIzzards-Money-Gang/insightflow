package br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs;

import br.com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ProdutoNaoEncontrado extends BaseException {
    public ProdutoNaoEncontrado() {
        super("Produto não encontrado.");
    }
}
