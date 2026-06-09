package com.cafe.projeto.dao.produto;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.ProdutoResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BuscarProdutoPorId implements OperacaoDao<Long, ProdutoResponse> {

    private final JdbcTemplate jdbcTemplate;

    public BuscarProdutoPorId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ProdutoResponse executar(Long entrada) {
        String sql = """
                select id, nome, preco, imagem_url
                from produto
                where id = ?
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new ProdutoResponse(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getBigDecimal("preco"),
                rs.getString("imagem_url")
        ), entrada);
    }
}
