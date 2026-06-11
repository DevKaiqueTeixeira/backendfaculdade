package com.cafe.projeto.decorator;

import com.cafe.projeto.dto.PedidoAdicionalResponse;

import java.math.BigDecimal;
import java.util.List;

public class PedidoBase implements Pedido {

    private final BigDecimal precoProduto;

    public PedidoBase(BigDecimal precoProduto) {
        this.precoProduto = precoProduto;
    }

    @Override
    public BigDecimal calcularValorTotal() {
        return precoProduto;
    }

    @Override
    public List<PedidoAdicionalResponse> listarAdicionais() {
        return List.of();
    }
}
