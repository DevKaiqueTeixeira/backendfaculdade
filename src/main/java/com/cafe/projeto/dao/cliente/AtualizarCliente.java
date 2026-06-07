package com.cafe.projeto.dao.cliente;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.ClienteAtualizacaoRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AtualizarCliente implements OperacaoDao<ClienteAtualizacaoRequest, Long> {

    private final JdbcTemplate jdbcTemplate;

    public AtualizarCliente(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(ClienteAtualizacaoRequest entrada) {
        String sql = """
                update cliente
                set nome = ?,
                    cpf = ?,
                    email = ?,
                    data_nascimento = ?
                where auth_user_id = ?
                returning id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                entrada.getNome(),
                entrada.getCpf(),
                entrada.getEmail(),
                entrada.getDataNascimento(),
                UUID.fromString(entrada.getAuthUserId())
        );
    }
}
