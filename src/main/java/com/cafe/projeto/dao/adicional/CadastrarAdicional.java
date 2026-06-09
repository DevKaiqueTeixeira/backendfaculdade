package com.cafe.projeto.dao.adicional;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.AdicionalCadastroRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CadastrarAdicional implements OperacaoDao<AdicionalCadastroRequest, Long> {

    private final JdbcTemplate jdbcTemplate;

    public CadastrarAdicional(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(AdicionalCadastroRequest entrada) {
        String sql = """
                insert into adicional (nome, preco, produto_id)
                values (?, ?, ?)
                returning id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                entrada.getNome(),
                entrada.getPreco(),
                entrada.getProdutoId()
        );
    }
}
