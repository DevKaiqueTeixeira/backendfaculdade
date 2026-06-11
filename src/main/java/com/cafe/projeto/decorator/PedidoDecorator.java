package com.cafe.projeto.decorator;

import com.cafe.projeto.dto.PedidoAdicionalResponse;

import java.math.BigDecimal;
import java.util.List;

public abstract class PedidoDecorator implements Pedido {

    private final Pedido pedido;

    protected PedidoDecorator(Pedido pedido) {
        this.pedido = pedido;
    }

    protected Pedido getPedido() {
        return pedido;
    }

    @Override
    public BigDecimal calcularValorTotal() {
        return pedido.calcularValorTotal();
    }

    @Override
    public List<PedidoAdicionalResponse> listarAdicionais() {
        return pedido.listarAdicionais();
    }
}
