package com.cafe.projeto.dao.adicional;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.AdicionalResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarAdicionaisPorProdutoId implements OperacaoDao<Long, List<AdicionalResponse>> {

    private final JdbcTemplate jdbcTemplate;

    public ListarAdicionaisPorProdutoId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AdicionalResponse> executar(Long entrada) {
        String sql = """
                select id, nome, preco, produto_id
                from adicional
                where produto_id = ?
                order by id desc
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new AdicionalResponse(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getBigDecimal("preco"),
                rs.getLong("produto_id")
        ), entrada);
    }
}
