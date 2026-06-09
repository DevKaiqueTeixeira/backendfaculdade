package com.cafe.projeto.dto;

import java.math.BigDecimal;

public class ProdutoResponse {

    private final Long id;
    private final String nome;
    private final BigDecimal preco;
    private final String imagemUrl;

    public ProdutoResponse(Long id, String nome, BigDecimal preco, String imagemUrl) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.imagemUrl = imagemUrl;
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

    public String getImagemUrl() {
        return imagemUrl;
    }
}
