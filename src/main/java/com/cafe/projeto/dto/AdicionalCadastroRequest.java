package com.cafe.projeto.dto;

import java.math.BigDecimal;

public class AdicionalCadastroRequest {

    private String nome;
    private BigDecimal preco;
    private Long produtoId;

    public AdicionalCadastroRequest() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }
}
