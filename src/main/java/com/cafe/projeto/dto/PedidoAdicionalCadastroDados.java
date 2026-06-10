package com.cafe.projeto.dto;

import java.math.BigDecimal;

public class PedidoAdicionalCadastroDados {

    private Long pedidoId;
    private Long adicionalId;
    private BigDecimal preco;

    public PedidoAdicionalCadastroDados(Long pedidoId, Long adicionalId, BigDecimal preco) {
        this.pedidoId = pedidoId;
        this.adicionalId = adicionalId;
        this.preco = preco;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public Long getAdicionalId() {
        return adicionalId;
    }

    public BigDecimal getPreco() {
        return preco;
    }
}
