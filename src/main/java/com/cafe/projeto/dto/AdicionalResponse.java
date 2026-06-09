package com.cafe.projeto.dto;

import java.math.BigDecimal;

public class AdicionalResponse {

    private final Long id;
    private final String nome;
    private final BigDecimal preco;
    private final Long produtoId;

    public AdicionalResponse(Long id, String nome, BigDecimal preco, Long produtoId) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.produtoId = produtoId;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Long getProdutoId() {
        return produtoId;
    }
}
