package com.cafe.projeto.dao.pedido;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.PedidoAdicionalResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ListarAdicionaisPorPedidoIds implements OperacaoDao<List<Long>, Map<Long, List<PedidoAdicionalResponse>>> {

    private final JdbcTemplate jdbcTemplate;

    public ListarAdicionaisPorPedidoIds(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, List<PedidoAdicionalResponse>> executar(List<Long> entrada) {
        if (entrada == null || entrada.isEmpty()) {
            return Collections.emptyMap();
        }

        String placeholders = String.join(", ", Collections.nCopies(entrada.size(), "?"));
        String sql = """
                select pedido_id, adicional_id, adicional_nome, preco
                from pedido_adicional
                where pedido_id in (%s)
                order by pedido_id asc, id asc
                """.formatted(placeholders);

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, List<PedidoAdicionalResponse>> adicionaisPorPedido = new LinkedHashMap<>();

            while (rs.next()) {
                Long pedidoId = rs.getLong("pedido_id");
                adicionaisPorPedido.computeIfAbsent(pedidoId, key -> new ArrayList<>()).add(new PedidoAdicionalResponse(
                        rs.getObject("adicional_id", Long.class),
                        rs.getString("adicional_nome"),
                        rs.getBigDecimal("preco")
                ));
            }

            return adicionaisPorPedido;
        }, entrada.toArray());
    }
}
