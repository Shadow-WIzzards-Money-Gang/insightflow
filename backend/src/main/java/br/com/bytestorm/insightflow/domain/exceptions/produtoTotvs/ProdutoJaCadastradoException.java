package br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs;

import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;

public class ProdutoJaCadastradoException extends RegraDeNegocioException {
    public ProdutoJaCadastradoException() {
        super("Produto já cadastrado.");
    }
}
