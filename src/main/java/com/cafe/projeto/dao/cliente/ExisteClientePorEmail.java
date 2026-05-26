package com.cafe.projeto.dao.cliente;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExisteClientePorEmail implements OperacaoDao<String, Boolean> {

    private final JdbcTemplate jdbcTemplate;

    public ExisteClientePorEmail(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean executar(String entrada) {
        String sql = "select exists(select 1 from cliente where email = ?)";
        Boolean resultado = jdbcTemplate.queryForObject(sql, Boolean.class, entrada);
        return Boolean.TRUE.equals(resultado);
    }
}
