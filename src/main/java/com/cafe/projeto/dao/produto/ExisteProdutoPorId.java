package com.cafe.projeto.dao.produto;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExisteProdutoPorId implements OperacaoDao<Long, Boolean> {

    private final JdbcTemplate jdbcTemplate;

    public ExisteProdutoPorId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean executar(Long entrada) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "select exists(select 1 from produto where id = ?)",
                Boolean.class,
                entrada
        ));
    }
}
