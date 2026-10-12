package br.com.bytestorm.insightflow.domain.entity;

import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;

public class ProdutoTotvs {

    private final Long id;
    private final String nome;
    private final String categoria;

    private ProdutoTotvs(Long id, String nome, String categoria) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
    }

    public static ProdutoTotvs novo(String nome, String categoria) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome do produto é obrigatório.");
        }
        if (categoria == null || categoria.isBlank()) {
            throw new RegraDeNegocioException("A categoria do produto é obrigatória.");
        }
        return new ProdutoTotvs(null, nome, categoria);
    }

    /** Remonta um produto ja persistido, sem revalidar as regras de criacao. */
    public static ProdutoTotvs reconstituir(Long id, String nome, String categoria) {
        return new ProdutoTotvs(id, nome, categoria);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }
}
