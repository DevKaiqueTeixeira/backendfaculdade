package com.cafe.projeto.dao.pedido;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.PedidoResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuscarPedidoPorId implements OperacaoDao<Long, PedidoResponse> {

    private final JdbcTemplate jdbcTemplate;

    public BuscarPedidoPorId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PedidoResponse executar(Long entrada) {
        String sql = """
                select p.id,
                       p.cliente_id,
                       p.produto_id,
                       pr.nome as produto_nome,
                       p.endereco_id,
                       p.preco_produto,
                       p.valor_total,
                       p.status,
                       p.criado_em
                from pedido p
                join produto pr on pr.id = p.produto_id
                where p.id = ?
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new PedidoResponse(
                rs.getLong("id"),
                rs.getLong("cliente_id"),
                rs.getLong("produto_id"),
                rs.getString("produto_nome"),
                rs.getObject("endereco_id", Long.class),
                rs.getBigDecimal("preco_produto"),
                rs.getBigDecimal("valor_total"),
                rs.getString("status"),
                rs.getTimestamp("criado_em").toLocalDateTime(),
                List.of()
        ), entrada);
    }
}
