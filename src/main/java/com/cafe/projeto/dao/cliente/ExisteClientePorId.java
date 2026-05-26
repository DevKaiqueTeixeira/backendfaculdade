package com.cafe.projeto.dao.cliente;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExisteClientePorId implements OperacaoDao<Long, Boolean> {

    private final JdbcTemplate jdbcTemplate;

    public ExisteClientePorId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean executar(Long entrada) {
        String sql = "select exists(select 1 from cliente where id = ?)";
        Boolean resultado = jdbcTemplate.queryForObject(sql, Boolean.class, entrada);
        return Boolean.TRUE.equals(resultado);
    }
}
