package com.cafe.projeto.dto;

import java.math.BigDecimal;

public class PedidoAdicionalResponse {

    private final Long adicionalId;
    private final String nome;
    private final BigDecimal preco;

    public PedidoAdicionalResponse(Long adicionalId, String nome, BigDecimal preco) {
        this.adicionalId = adicionalId;
        this.nome = nome;
        this.preco = preco;
    }

    public Long getAdicionalId() {
        return adicionalId;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }
}
