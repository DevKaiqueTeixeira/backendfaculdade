package com.cafe.projeto.dao.endereco;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.EnderecoAtualizacaoRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AtualizarEndereco implements OperacaoDao<EnderecoAtualizacaoRequest, Long> {

    private final JdbcTemplate jdbcTemplate;

    public AtualizarEndereco(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(EnderecoAtualizacaoRequest entrada) {
        String sql = """
                update endereco
                set cep = ?,
                    logradouro = ?,
                    numero = ?,
                    complemento = ?,
                    bairro = ?,
                    cidade = ?,
                    estado = ?,
                    pais = ?,
                    ponto_referencia = ?,
                    tipo_endereco = ?
                where id = ?
                  and cliente_id = ?
                returning id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                entrada.getCep(),
                entrada.getLogradouro(),
                entrada.getNumero(),
                entrada.getComplemento(),
                entrada.getBairro(),
                entrada.getCidade(),
                entrada.getEstado(),
                entrada.getPais(),
                entrada.getPontoReferencia(),
                entrada.getTipoEndereco(),
                entrada.getId(),
                entrada.getClienteId()
        );
    }
}
