package com.cafe.projeto.dao.endereco;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.EnderecoCadastroRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CadastrarEndereco implements OperacaoDao<EnderecoCadastroRequest, Long> {

    private final JdbcTemplate jdbcTemplate;

    public CadastrarEndereco(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(EnderecoCadastroRequest entrada) {
        String sql = """
                insert into endereco (
                    cliente_id,
                    cep,
                    logradouro,
                    numero,
                    complemento,
                    bairro,
                    cidade,
                    estado,
                    pais,
                    ponto_referencia,
                    tipo_endereco
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                returning id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                entrada.getClienteId(),
                entrada.getCep(),
                entrada.getLogradouro(),
                entrada.getNumero(),
                entrada.getComplemento(),
                entrada.getBairro(),
                entrada.getCidade(),
                entrada.getEstado(),
                entrada.getPais(),
                entrada.getPontoReferencia(),
                entrada.getTipoEndereco()
        );
    }
}
