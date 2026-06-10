package com.cafe.projeto.command;

import com.cafe.projeto.dto.AdicionalResponse;

import java.math.BigDecimal;

public class AdicionarAdicionalCommand implements PedidoCommand {

    private final AdicionalResponse adicional;

    public AdicionarAdicionalCommand(AdicionalResponse adicional) {
        this.adicional = adicional;
    }

    @Override
    public BigDecimal executar(BigDecimal valorAtual) {
        return valorAtual.add(adicional.getPreco());
    }

    public AdicionalResponse getAdicional() {
        return adicional;
    }
}
