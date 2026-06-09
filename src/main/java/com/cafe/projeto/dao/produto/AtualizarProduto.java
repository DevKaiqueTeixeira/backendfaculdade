package com.cafe.projeto.dao.produto;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.ProdutoAtualizacaoRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AtualizarProduto implements OperacaoDao<ProdutoAtualizacaoRequest, Long> {

    private final JdbcTemplate jdbcTemplate;

    public AtualizarProduto(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long executar(ProdutoAtualizacaoRequest entrada) {
        String sql = """
                update produto
                set nome = ?,
                    preco = ?,
                    imagem_url = ?
                where id = ?
                returning id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                entrada.getNome(),
                entrada.getPreco(),
                entrada.getImagemUrl(),
                entrada.getId()
        );
    }
}
