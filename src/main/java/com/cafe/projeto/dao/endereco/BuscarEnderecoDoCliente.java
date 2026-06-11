package com.cafe.projeto.dao.endereco;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.EnderecoClienteConsulta;
import com.cafe.projeto.dto.EnderecoResponse;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BuscarEnderecoDoCliente implements OperacaoDao<EnderecoClienteConsulta, EnderecoResponse> {

    private final JdbcTemplate jdbcTemplate;

    public BuscarEnderecoDoCliente(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public EnderecoResponse executar(EnderecoClienteConsulta entrada) {
        String sql = """
                select id, cep, logradouro, numero, complemento, bairro, cidade, estado, pais, ponto_referencia, tipo_endereco
                from endereco
                where id = ? and cliente_id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new EnderecoResponse(
                    rs.getLong("id"),
                    rs.getString("cep"),
                    rs.getString("logradouro"),
                    rs.getString("numero"),
                    rs.getString("complemento"),
                    rs.getString("bairro"),
                    rs.getString("cidade"),
                    rs.getString("estado"),
                    rs.getString("pais"),
                    rs.getString("ponto_referencia"),
                    rs.getString("tipo_endereco")
            ), entrada.getEnderecoId(), entrada.getClienteId());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
