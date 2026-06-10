package com.cafe.projeto.command;

import java.math.BigDecimal;

public interface PedidoCommand {

    BigDecimal executar(BigDecimal valorAtual);
}
