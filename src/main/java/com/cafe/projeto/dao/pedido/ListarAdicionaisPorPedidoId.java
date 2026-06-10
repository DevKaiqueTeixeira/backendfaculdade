package com.cafe.projeto.dao.pedido;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.AdicionalResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarAdicionaisPorPedidoId implements OperacaoDao<Long, List<AdicionalResponse>> {

    private final JdbcTemplate jdbcTemplate;

    public ListarAdicionaisPorPedidoId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AdicionalResponse> executar(Long entrada) {
        String sql = """
                select a.id, a.nome, pa.preco, a.produto_id
                from pedido_adicional pa
                join adicional a on a.id = pa.adicional_id
                where pa.pedido_id = ?
                order by a.nome
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new AdicionalResponse(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getBigDecimal("preco"),
                rs.getLong("produto_id")
        ), entrada);
    }
}
