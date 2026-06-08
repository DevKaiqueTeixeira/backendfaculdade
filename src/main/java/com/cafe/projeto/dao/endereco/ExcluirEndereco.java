package com.cafe.projeto.dao.endereco;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.EnderecoAtualizacaoRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExcluirEndereco implements OperacaoDao<EnderecoAtualizacaoRequest, Long> {

    private final JdbcTemplate jdbcTemplate;

    public ExcluirEndereco(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(EnderecoAtualizacaoRequest entrada) {
        return jdbcTemplate.queryForObject(
                "delete from endereco where id = ? and cliente_id = ? returning id",
                Long.class,
                entrada.getId(),
                entrada.getClienteId()
        );
    }
}
