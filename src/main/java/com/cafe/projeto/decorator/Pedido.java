package com.cafe.projeto.decorator;

import com.cafe.projeto.dto.PedidoAdicionalResponse;

import java.math.BigDecimal;
import java.util.List;

public interface Pedido {

    BigDecimal calcularValorTotal();

    List<PedidoAdicionalResponse> listarAdicionais();
}
