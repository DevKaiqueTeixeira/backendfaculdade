package com.cafe.projeto.command;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PedidoCommander {

    public BigDecimal executar(BigDecimal valorInicial, List<? extends PedidoCommand> comandos) {
        BigDecimal valorAtual = valorInicial;

        for (PedidoCommand comando : comandos) {
            valorAtual = comando.executar(valorAtual);
        }

        return valorAtual;
    }
}
