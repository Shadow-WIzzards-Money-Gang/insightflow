package br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs;

import br.com.bytestorm.insightflow.domain.exceptions.BaseException;

public class ProdutoJaCadastrado extends BaseException {
    public ProdutoJaCadastrado() {
        super("Produto já cadastrado.");
    }
}
