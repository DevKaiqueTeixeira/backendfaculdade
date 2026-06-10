package com.cafe.projeto.dao.pedido;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.PedidoCadastroDados;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CadastrarPedido implements OperacaoDao<PedidoCadastroDados, Long> {

    private final JdbcTemplate jdbcTemplate;

    public CadastrarPedido(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(PedidoCadastroDados entrada) {
        String sql = """
                insert into pedido (cliente_id, produto_id, endereco_id, preco_produto, valor_total, status)
                values (?, ?, ?, ?, ?, ?)
                returning id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                entrada.getClienteId(),
                entrada.getProdutoId(),
                entrada.getEnderecoId(),
                entrada.getPrecoProduto(),
                entrada.getValorTotal(),
                entrada.getStatus()
        );
    }
}
