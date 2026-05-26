package com.cafe.projeto.dao.cliente;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.ClienteCadastroRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CadastrarCliente implements OperacaoDao<ClienteCadastroRequest, Long> {

    private final JdbcTemplate jdbcTemplate;

    public CadastrarCliente(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(ClienteCadastroRequest entrada) {
        String sql = """
                insert into cliente (nome, cpf, auth_user_id, email, data_nascimento)
                values (?, ?, ?, ?, ?)
                returning id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                entrada.getNome(),
                entrada.getCpf(),
                java.util.UUID.fromString(entrada.getAuthUserId()),
                entrada.getEmail(),
                entrada.getDataNascimento()
        );
    }
}
