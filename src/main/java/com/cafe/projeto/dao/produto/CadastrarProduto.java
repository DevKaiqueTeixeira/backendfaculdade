package com.cafe.projeto.dao.produto;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.ProdutoCadastroRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CadastrarProduto implements OperacaoDao<ProdutoCadastroRequest, Long> {

    private final JdbcTemplate jdbcTemplate;

    public CadastrarProduto(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(ProdutoCadastroRequest entrada) {
        String sql = """
                insert into produto (nome, preco)
                values (?, ?)
                returning id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                entrada.getNome(),
                entrada.getPreco()
        );
    }
}
