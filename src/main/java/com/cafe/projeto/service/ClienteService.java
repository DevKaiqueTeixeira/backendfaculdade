package com.cafe.projeto.service;

import com.cafe.projeto.dao.cliente.CadastrarCliente;
import com.cafe.projeto.dao.cliente.ExisteClientePorAuthUserId;
import com.cafe.projeto.dao.cliente.ExisteClientePorCpf;
import com.cafe.projeto.dao.cliente.ExisteClientePorEmail;
import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.ClienteCadastroRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ClienteService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final CadastrarCliente cadastrarCliente;
    private final ExisteClientePorAuthUserId existeClientePorAuthUserId;
    private final ExisteClientePorCpf existeClientePorCpf;
    private final ExisteClientePorEmail existeClientePorEmail;

    public ClienteService(
            CadastrarCliente cadastrarCliente,
            ExisteClientePorAuthUserId existeClientePorAuthUserId,
            ExisteClientePorCpf existeClientePorCpf,
            ExisteClientePorEmail existeClientePorEmail
    ) {
        this.cadastrarCliente = cadastrarCliente;
        this.existeClientePorAuthUserId = existeClientePorAuthUserId;
        this.existeClientePorCpf = existeClientePorCpf;
        this.existeClientePorEmail = existeClientePorEmail;
    }

    public CadastroResponse cadastrar(ClienteCadastroRequest request) {
        validarRequest(request);

        request.setNome(request.getNome().trim());
        request.setCpf(request.getCpf().trim());
        request.setAuthUserId(request.getAuthUserId().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());

        UUID authUserId = UUID.fromString(request.getAuthUserId());

        if (existeClientePorAuthUserId.executar(authUserId)) {
            throw new ValidacaoException("Usuario ja vinculado a um cliente.");
        }

        if (existeClientePorCpf.executar(request.getCpf())) {
            throw new ValidacaoException("CPF ja cadastrado.");
        }

        if (existeClientePorEmail.executar(request.getEmail())) {
            throw new ValidacaoException("Email ja cadastrado.");
        }

        Long id = cadastrarCliente.executar(request);
        return new CadastroResponse(id, "Cliente cadastrado com sucesso.");
    }

    private void validarRequest(ClienteCadastroRequest request) {
        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        validarCampoObrigatorio(request.getNome(), "Nome");
        validarCampoObrigatorio(request.getCpf(), "CPF");
        validarCampoObrigatorio(request.getAuthUserId(), "Auth user id");
        validarCampoObrigatorio(request.getEmail(), "Email");

        if (request.getDataNascimento() == null) {
            throw new ValidacaoException("Data de nascimento e obrigatoria.");
        }

        validarEmail(request.getEmail().trim());
        validarAuthUserId(request.getAuthUserId().trim());
    }

    private void validarCampoObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException(campo + " e obrigatorio.");
        }
    }

    private void validarEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidacaoException("Email invalido.");
        }
    }

    private void validarAuthUserId(String authUserId) {
        try {
            UUID.fromString(authUserId);
        } catch (IllegalArgumentException ex) {
            throw new ValidacaoException("Auth user id invalido.");
        }
    }
}
