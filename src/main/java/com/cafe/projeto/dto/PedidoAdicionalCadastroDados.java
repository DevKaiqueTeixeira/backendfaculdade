package com.cafe.projeto.dto;

import java.math.BigDecimal;

public class PedidoAdicionalCadastroDados {

    private final Long pedidoId;
    private final Long adicionalId;
    private final String adicionalNome;
    private final BigDecimal preco;

    public PedidoAdicionalCadastroDados(Long pedidoId, Long adicionalId, String adicionalNome, BigDecimal preco) {
        this.pedidoId = pedidoId;
        this.adicionalId = adicionalId;
        this.adicionalNome = adicionalNome;
        this.preco = preco;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public Long getAdicionalId() {
        return adicionalId;
    }

    public String getAdicionalNome() {
        return adicionalNome;
    }

    public BigDecimal getPreco() {
        return preco;
    }
}
