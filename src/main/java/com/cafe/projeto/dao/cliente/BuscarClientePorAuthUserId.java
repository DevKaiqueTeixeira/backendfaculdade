package com.cafe.projeto.dao.cliente;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.model.Cliente;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class BuscarClientePorAuthUserId implements OperacaoDao<UUID, Cliente> {

    private final JdbcTemplate jdbcTemplate;

    public BuscarClientePorAuthUserId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Cliente executar(UUID entrada) {
        String sql = """
                select id, nome, cpf, auth_user_id, email, data_nascimento
                from cliente
                where auth_user_id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getLong("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setAuthUserId(rs.getObject("auth_user_id", UUID.class));
                cliente.setEmail(rs.getString("email"));
                cliente.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));
                return cliente;
            }, entrada);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
