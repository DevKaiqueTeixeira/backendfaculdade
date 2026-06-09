package com.cafe.projeto.dao.produto;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.ProdutoResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarProdutos implements OperacaoDao<Void, List<ProdutoResponse>> {

    private final JdbcTemplate jdbcTemplate;

    public ListarProdutos(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ProdutoResponse> executar(Void entrada) {
        String sql = """
                select id, nome, preco
                from produto
                order by id desc
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new ProdutoResponse(
                rs.getLong("id"),
                rs.getString("nome"),
                rs.getBigDecimal("preco")
        ));
    }
}
