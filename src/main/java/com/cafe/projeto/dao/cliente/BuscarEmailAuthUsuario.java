package com.cafe.projeto.dao.cliente;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BuscarEmailAuthUsuario implements OperacaoDao<UUID, String> {

    private final JdbcTemplate jdbcTemplate;

    public BuscarEmailAuthUsuario(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String executar(UUID entrada) {
        try {
            return jdbcTemplate.queryForObject(
                    "select email from auth.users where id = ?",
                    String.class,
                    entrada
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
