package com.cafe.projeto.dao.pedido;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.PedidoAdicionalCadastroDados;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CadastrarPedidoAdicional implements OperacaoDao<PedidoAdicionalCadastroDados, Void> {

    private final JdbcTemplate jdbcTemplate;

    public CadastrarPedidoAdicional(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Void executar(PedidoAdicionalCadastroDados entrada) {
        String sql = """
                insert into pedido_adicional (pedido_id, adicional_id, adicional_nome, preco)
                values (?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                entrada.getPedidoId(),
                entrada.getAdicionalId(),
                entrada.getAdicionalNome(),
                entrada.getPreco()
        );

        return null;
    }
}
