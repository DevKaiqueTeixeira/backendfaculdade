package com.cafe.projeto.dao.pedido;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.PedidoCadastroDados;
import com.cafe.projeto.dto.PedidoCadastroResultado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CadastrarPedido implements OperacaoDao<PedidoCadastroDados, PedidoCadastroResultado> {

    private final JdbcTemplate jdbcTemplate;

    public CadastrarPedido(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PedidoCadastroResultado executar(PedidoCadastroDados entrada) {
        String sql = """
                insert into pedido (
                    cliente_id,
                    produto_id,
                    produto_nome,
                    endereco_id,
                    endereco_resumo,
                    preco_produto,
                    valor_total,
                    status
                )
                values (?, ?, ?, ?, ?, ?, ?, ?)
                returning id, criado_em
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> new PedidoCadastroResultado(
                        rs.getLong("id"),
                        rs.getTimestamp("criado_em").toLocalDateTime()
                ),
                entrada.getClienteId(),
                entrada.getProdutoId(),
                entrada.getProdutoNome(),
                entrada.getEnderecoId(),
                entrada.getEnderecoResumo(),
                entrada.getPrecoProduto(),
                entrada.getValorTotal(),
                entrada.getStatus()
        );
    }
}
