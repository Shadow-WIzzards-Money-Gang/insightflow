package br.com.bytestorm.insightflow.domain.exceptions.produtoTotvs;

import br.com.bytestorm.insightflow.domain.exceptions.erro.Erro400Exception;

public class ProdutoJaCadastradoException extends Erro400Exception {
    public ProdutoJaCadastradoException() {
        super("Produto já cadastrado.");
    }
}
