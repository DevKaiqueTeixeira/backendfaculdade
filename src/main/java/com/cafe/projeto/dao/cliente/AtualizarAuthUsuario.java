package com.cafe.projeto.dao.cliente;

import com.cafe.projeto.dao.operacao.OperacaoDao;
import com.cafe.projeto.dto.ClienteAtualizacaoRequest;
import com.cafe.projeto.service.ValidacaoException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class AtualizarAuthUsuario implements OperacaoDao<ClienteAtualizacaoRequest, Void> {

    private final JdbcTemplate jdbcTemplate;

    public AtualizarAuthUsuario(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Void executar(ClienteAtualizacaoRequest entrada) {
        UUID authUserId = UUID.fromString(entrada.getAuthUserId());
        Timestamp agora = Timestamp.from(Instant.now());

        int usuariosAtualizados = jdbcTemplate.update(
                """
                        update auth.users
                        set email = ?,
                            raw_user_meta_data = coalesce(raw_user_meta_data, '{}'::jsonb)
                                || jsonb_build_object(
                                    'nome', ?,
                                    'cpf', ?,
                                    'dataNascimento', ?
                                ),
                            updated_at = ?
                        where id = ?
                        """,
                entrada.getEmail(),
                entrada.getNome(),
                entrada.getCpf(),
                entrada.getDataNascimento().toString(),
                agora,
                authUserId
        );

        if (usuariosAtualizados == 0) {
            throw new ValidacaoException("Usuario autenticado nao encontrado no Auth.");
        }

        int identidadesAtualizadas = jdbcTemplate.update(
                """
                        update auth.identities
                        set identity_data = identity_data
                            || jsonb_build_object('sub', ?, 'email', ?),
                            updated_at = ?
                        where user_id = ?
                          and provider = 'email'
                        """,
                authUserId.toString(),
                entrada.getEmail(),
                agora,
                authUserId
        );

        if (identidadesAtualizadas == 0) {
            jdbcTemplate.update(
                    """
                            insert into auth.identities (id, user_id, identity_data, provider, last_sign_in_at, created_at, updated_at)
                            values (?, ?, jsonb_build_object('sub', ?, 'email', ?), 'email', null, ?, ?)
                            """,
                    authUserId.toString(),
                    authUserId,
                    authUserId.toString(),
                    entrada.getEmail(),
                    agora,
                    agora
            );
        }

        return null;
    }
}
