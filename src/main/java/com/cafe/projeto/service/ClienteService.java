package com.cafe.projeto.service;

import com.cafe.projeto.dao.cliente.AtualizarAuthUsuario;
import com.cafe.projeto.dao.cliente.AtualizarCliente;
import com.cafe.projeto.dao.cliente.BuscarClientePorAuthUserId;
import com.cafe.projeto.dao.cliente.BuscarEmailAuthUsuario;
import com.cafe.projeto.dao.cliente.CadastrarCliente;
import com.cafe.projeto.dao.cliente.ExisteClientePorAuthUserId;
import com.cafe.projeto.dao.cliente.ExisteClientePorCpf;
import com.cafe.projeto.dao.cliente.ExisteClientePorEmail;
import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.ClienteCadastroRequest;
import com.cafe.projeto.dto.ClienteAtualizacaoRequest;
import com.cafe.projeto.dto.ClientePerfilResponse;
import com.cafe.projeto.model.Cliente;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ClienteService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final AtualizarAuthUsuario atualizarAuthUsuario;
    private final AtualizarCliente atualizarCliente;
    private final BuscarClientePorAuthUserId buscarClientePorAuthUserId;
    private final BuscarEmailAuthUsuario buscarEmailAuthUsuario;
    private final CadastrarCliente cadastrarCliente;
    private final ExisteClientePorAuthUserId existeClientePorAuthUserId;
    private final ExisteClientePorCpf existeClientePorCpf;
    private final ExisteClientePorEmail existeClientePorEmail;
    private final SupabaseAdminAuthService supabaseAdminAuthService;

    public ClienteService(
            AtualizarAuthUsuario atualizarAuthUsuario,
            AtualizarCliente atualizarCliente,
            BuscarClientePorAuthUserId buscarClientePorAuthUserId,
            BuscarEmailAuthUsuario buscarEmailAuthUsuario,
            CadastrarCliente cadastrarCliente,
            ExisteClientePorAuthUserId existeClientePorAuthUserId,
            ExisteClientePorCpf existeClientePorCpf,
            ExisteClientePorEmail existeClientePorEmail,
            SupabaseAdminAuthService supabaseAdminAuthService
    ) {
        this.atualizarAuthUsuario = atualizarAuthUsuario;
        this.atualizarCliente = atualizarCliente;
        this.buscarClientePorAuthUserId = buscarClientePorAuthUserId;
        this.buscarEmailAuthUsuario = buscarEmailAuthUsuario;
        this.cadastrarCliente = cadastrarCliente;
        this.existeClientePorAuthUserId = existeClientePorAuthUserId;
        this.existeClientePorCpf = existeClientePorCpf;
        this.existeClientePorEmail = existeClientePorEmail;
        this.supabaseAdminAuthService = supabaseAdminAuthService;
    }

    public CadastroResponse cadastrar(ClienteCadastroRequest request) {
        validarBodyRequest(request);

        validarDadosCliente(
                request.getNome(),
                request.getCpf(),
                request.getAuthUserId(),
                request.getEmail(),
                request.getDataNascimento()
        );

        normalizarCadastroRequest(request);

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

        Long id;

        try {
            id = cadastrarCliente.executar(request);
        } catch (DataIntegrityViolationException ex) {
            throw traduzirErroIntegridade(ex);
        }

        return new CadastroResponse(id, "Cliente cadastrado com sucesso.");
    }

    @Transactional
    public CadastroResponse atualizar(String authorization, ClienteAtualizacaoRequest request) {
        validarBodyRequest(request);

        request.setAuthUserId(supabaseAdminAuthService.buscarUsuarioIdPorAccessToken(extrairAccessToken(authorization)));

        validarDadosCliente(
                request.getNome(),
                request.getCpf(),
                request.getAuthUserId(),
                request.getEmail(),
                request.getDataNascimento()
        );

        normalizarAtualizacaoRequest(request);

        Cliente clienteAtual = buscarClientePorAuthUserId.executar(UUID.fromString(request.getAuthUserId()));

        if (clienteAtual == null) {
            throw new ValidacaoException("Cliente nao encontrado.");
        }

        if (!request.getCpf().equals(normalizarCpf(clienteAtual.getCpf())) && existeClientePorCpf.executar(request.getCpf())) {
            throw new ValidacaoException("CPF ja cadastrado.");
        }

        if (!request.getEmail().equals(clienteAtual.getEmail()) && existeClientePorEmail.executar(request.getEmail())) {
            throw new ValidacaoException("Email ja cadastrado.");
        }

        String emailAuthAtual = buscarEmailAuthUsuario.executar(UUID.fromString(request.getAuthUserId()));

        if (emailAuthAtual == null) {
            throw new ValidacaoException("Usuario autenticado nao encontrado no Auth.");
        }

        boolean perfilAlterado = houveAlteracao(request, clienteAtual);
        boolean emailAuthDivergente = !request.getEmail().equalsIgnoreCase(emailAuthAtual);

        Long id;

        try {
            if (!perfilAlterado && !emailAuthDivergente) {
                return new CadastroResponse(clienteAtual.getId(), "Nenhum dado foi alterado.");
            }

            atualizarAuthUsuario.executar(request);

            if (!perfilAlterado) {
                return new CadastroResponse(clienteAtual.getId(), "Perfil sincronizado com sucesso.");
            }

            id = atualizarCliente.executar(request);
        } catch (DataIntegrityViolationException ex) {
            throw traduzirErroIntegridade(ex);
        }

        return new CadastroResponse(id, "Cliente atualizado com sucesso.");
    }

    public ClientePerfilResponse buscarPerfil(String authorization) {
        String authUserId = supabaseAdminAuthService.buscarUsuarioIdPorAccessToken(extrairAccessToken(authorization));
        Cliente cliente = buscarClientePorAuthUserId.executar(UUID.fromString(authUserId));

        if (cliente == null) {
            throw new ValidacaoException("Cliente nao encontrado.");
        }

        return mapearPerfil(cliente);
    }

    private void validarBodyRequest(Object request) {
        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }
    }

    private String extrairAccessToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new AutorizacaoException("Token de acesso obrigatorio.");
        }

        String prefixo = "Bearer ";

        if (!authorization.startsWith(prefixo) || authorization.length() <= prefixo.length()) {
            throw new AutorizacaoException("Header Authorization invalido.");
        }

        return authorization.substring(prefixo.length()).trim();
    }

    private void validarDadosCliente(
            String nome,
            String cpf,
            String authUserId,
            String email,
            LocalDate dataNascimento
    ) {
        validarCampoObrigatorio(nome, "Nome");
        validarCampoObrigatorio(cpf, "CPF");
        validarCampoObrigatorio(authUserId, "Auth user id");
        validarCampoObrigatorio(email, "Email");
        EmojiValidationUtils.validarSemEmoji(nome, "nome");
        EmojiValidationUtils.validarSemEmoji(cpf, "cpf");
        EmojiValidationUtils.validarSemEmoji(email, "email");
        validarCpf(cpf);

        if (dataNascimento == null) {
            throw new ValidacaoException("Data de nascimento e obrigatoria.");
        }

        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new ValidacaoException("Data de nascimento invalida.");
        }

        validarEmail(email.trim());
        validarAuthUserId(authUserId.trim());
    }

    private ValidacaoException traduzirErroIntegridade(DataIntegrityViolationException ex) {
        String mensagem = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage().toLowerCase()
                : "";

        if (mensagem.contains("cpf")) {
            return new ValidacaoException("CPF ja cadastrado.");
        }

        if (mensagem.contains("email")) {
            return new ValidacaoException("Email ja cadastrado.");
        }

        return new ValidacaoException("Nao foi possivel salvar os dados do cliente.");
    }

    private ClientePerfilResponse mapearPerfil(Cliente cliente) {
        return new ClientePerfilResponse(
                cliente.getId(),
                cliente.getNome(),
                normalizarCpf(cliente.getCpf()),
                cliente.getAuthUserId().toString(),
                cliente.getEmail(),
                cliente.getDataNascimento()
        );
    }

    private void normalizarCadastroRequest(ClienteCadastroRequest request) {
        request.setNome(request.getNome().trim());
        request.setCpf(normalizarCpf(request.getCpf()));
        request.setAuthUserId(request.getAuthUserId().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
    }

    private void normalizarAtualizacaoRequest(ClienteAtualizacaoRequest request) {
        request.setNome(request.getNome().trim());
        request.setCpf(normalizarCpf(request.getCpf()));
        request.setAuthUserId(request.getAuthUserId().trim());
        request.setEmail(request.getEmail().trim().toLowerCase());
    }

    private boolean houveAlteracao(ClienteAtualizacaoRequest request, Cliente clienteAtual) {
        return !request.getNome().equals(clienteAtual.getNome())
                || !request.getCpf().equals(normalizarCpf(clienteAtual.getCpf()))
                || !request.getEmail().equals(clienteAtual.getEmail())
                || !request.getDataNascimento().equals(clienteAtual.getDataNascimento());
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

    private void validarCpf(String cpf) {
        if (normalizarCpf(cpf).length() != 11) {
            throw new ValidacaoException("CPF invalido.");
        }
    }

    private String normalizarCpf(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    private void validarAuthUserId(String authUserId) {
        try {
            UUID.fromString(authUserId);
        } catch (IllegalArgumentException ex) {
            throw new ValidacaoException("Auth user id invalido.");
        }
    }
}
