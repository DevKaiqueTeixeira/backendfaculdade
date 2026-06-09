package com.cafe.projeto.dao.produto;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExcluirProduto implements OperacaoDao<Long, Long> {

    private final JdbcTemplate jdbcTemplate;

    public ExcluirProduto(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(Long entrada) {
        return jdbcTemplate.queryForObject(
                "delete from produto where id = ? returning id",
                Long.class,
                entrada
        );
    }
}
