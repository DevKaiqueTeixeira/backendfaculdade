package com.cafe.projeto.dao.pedido;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.PedidoResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarPedidosPorClienteId implements OperacaoDao<Long, List<PedidoResponse>> {

    private final JdbcTemplate jdbcTemplate;

    public ListarPedidosPorClienteId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PedidoResponse> executar(Long entrada) {
        String sql = """
                select id,
                       cliente_id,
                       produto_id,
                       produto_nome,
                       endereco_id,
                       endereco_resumo,
                       preco_produto,
                       valor_total,
                       status,
                       criado_em
                from pedido
                where cliente_id = ?
                order by criado_em desc, id desc
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new PedidoResponse(
                rs.getLong("id"),
                rs.getLong("cliente_id"),
                rs.getLong("produto_id"),
                rs.getString("produto_nome"),
                rs.getObject("endereco_id", Long.class),
                rs.getString("endereco_resumo"),
                rs.getBigDecimal("preco_produto"),
                rs.getBigDecimal("valor_total"),
                rs.getString("status"),
                rs.getTimestamp("criado_em").toLocalDateTime(),
                List.of()
        ), entrada);
    }
}
