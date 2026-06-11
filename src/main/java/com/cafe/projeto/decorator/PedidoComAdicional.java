package com.cafe.projeto.decorator;

import com.cafe.projeto.dto.PedidoAdicionalResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PedidoComAdicional extends PedidoDecorator {

    private final PedidoAdicionalResponse adicional;

    public PedidoComAdicional(Pedido pedido, PedidoAdicionalResponse adicional) {
        super(pedido);
        this.adicional = adicional;
    }

    @Override
    public BigDecimal calcularValorTotal() {
        return getPedido().calcularValorTotal().add(adicional.getPreco());
    }

    @Override
    public List<PedidoAdicionalResponse> listarAdicionais() {
        List<PedidoAdicionalResponse> adicionais = new ArrayList<>(getPedido().listarAdicionais());
        adicionais.add(adicional);
        return adicionais;
    }
}
