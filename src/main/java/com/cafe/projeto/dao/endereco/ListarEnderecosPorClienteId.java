package com.cafe.projeto.dao.endereco;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.EnderecoResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarEnderecosPorClienteId implements OperacaoDao<Long, List<EnderecoResponse>> {

    private final JdbcTemplate jdbcTemplate;

    public ListarEnderecosPorClienteId(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EnderecoResponse> executar(Long entrada) {
        String sql = """
                select id, cep, logradouro, numero, complemento, bairro, cidade, estado, pais, ponto_referencia, tipo_endereco
                from endereco
                where cliente_id = ?
                order by id desc
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new EnderecoResponse(
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
        ), entrada);
    }
}
