package com.cafe.projeto.dao.endereco;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.EnderecoClienteConsulta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExisteEnderecoDoCliente implements OperacaoDao<EnderecoClienteConsulta, Boolean> {

    private final JdbcTemplate jdbcTemplate;

    public ExisteEnderecoDoCliente(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean executar(EnderecoClienteConsulta entrada) {
        Integer total = jdbcTemplate.queryForObject(
                "select count(*) from endereco where id = ? and cliente_id = ?",
                Integer.class,
                entrada.getEnderecoId(),
                entrada.getClienteId()
        );

        return total != null && total > 0;
    }
}
