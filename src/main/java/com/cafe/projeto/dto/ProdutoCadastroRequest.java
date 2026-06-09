package com.cafe.projeto.dto;

import java.math.BigDecimal;

public class ProdutoCadastroRequest {

    private String nome;
    private BigDecimal preco;

    public ProdutoCadastroRequest() {
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
}
