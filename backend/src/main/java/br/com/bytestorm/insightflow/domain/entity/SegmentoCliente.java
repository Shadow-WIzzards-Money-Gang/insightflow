package br.com.bytestorm.insightflow.domain.entity;

import br.com.bytestorm.insightflow.domain.exceptions.erro.RegraDeNegocioException;

public class SegmentoCliente {

    private final Long id;
    private final String nome;

    private SegmentoCliente(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static SegmentoCliente novo(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome do segmento de cliente é obrigatório.");
        }
        return new SegmentoCliente(null, nome);
    }

    /** Remonta um segmento ja persistido, sem revalidar as regras de criacao. */
    public static SegmentoCliente reconstituir(Long id, String nome) {
        return new SegmentoCliente(id, nome);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
