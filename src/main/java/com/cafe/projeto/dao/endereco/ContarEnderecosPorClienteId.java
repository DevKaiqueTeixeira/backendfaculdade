package com.cafe.projeto.dao.endereco;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ContarEnderecosPorClienteId implements OperacaoDao<Long, Integer> {

    private final JdbcTemplate jdbcTemplate;

    public ContarEnderecosPorClienteId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer executar(Long entrada) {
        Integer total = jdbcTemplate.queryForObject(
                "select count(*) from endereco where cliente_id = ?",
                Integer.class,
                entrada
        );

        return total == null ? 0 : total;
    }
}
