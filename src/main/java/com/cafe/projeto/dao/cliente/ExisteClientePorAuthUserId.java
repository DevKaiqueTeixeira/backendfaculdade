package com.cafe.projeto.dao.cliente;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExisteClientePorAuthUserId implements OperacaoDao<UUID, Boolean> {

    private final JdbcTemplate jdbcTemplate;

    public ExisteClientePorAuthUserId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean executar(UUID entrada) {
        String sql = "select exists(select 1 from cliente where auth_user_id = ?)";
        Boolean resultado = jdbcTemplate.queryForObject(sql, Boolean.class, entrada);
        return Boolean.TRUE.equals(resultado);
    }
}
