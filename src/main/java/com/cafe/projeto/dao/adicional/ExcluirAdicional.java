package com.cafe.projeto.dao.adicional;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExcluirAdicional implements OperacaoDao<Long, Long> {

    private final JdbcTemplate jdbcTemplate;

    public ExcluirAdicional(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(Long entrada) {
        return jdbcTemplate.queryForObject(
                "delete from adicional where id = ? returning id",
                Long.class,
                entrada
        );
    }
}
