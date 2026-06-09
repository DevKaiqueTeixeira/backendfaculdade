package com.cafe.projeto.service;

import com.cafe.projeto.dto.AuthCadastroRequest;
import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.ClienteCadastroRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthCadastroService {

    private final SupabaseAdminAuthService supabaseAdminAuthService;
    private final ClienteService clienteService;

    public AuthCadastroService(SupabaseAdminAuthService supabaseAdminAuthService, ClienteService clienteService) {
        this.supabaseAdminAuthService = supabaseAdminAuthService;
        this.clienteService = clienteService;
    }

    public CadastroResponse cadastrar(AuthCadastroRequest request) {
        validarRequest(request);

        String authUserId = supabaseAdminAuthService.criarUsuario(
                request.getEmail().trim().toLowerCase(),
                request.getSenha().trim(),
                Map.of(
                        "nome", request.getNome().trim(),
                        "cpf", request.getCpf().trim(),
                        "dataNascimento", request.getDataNascimento().toString()
                )
        );

        try {
            ClienteCadastroRequest clienteRequest = new ClienteCadastroRequest();
            clienteRequest.setNome(request.getNome());
            clienteRequest.setCpf(request.getCpf());
            clienteRequest.setAuthUserId(authUserId);
            clienteRequest.setEmail(request.getEmail());
            clienteRequest.setDataNascimento(request.getDataNascimento());

            return clienteService.cadastrar(clienteRequest);
        } catch (RuntimeException ex) {
            supabaseAdminAuthService.deletarUsuario(authUserId);
            throw ex;
        }
    }

    private void validarRequest(AuthCadastroRequest request) {
        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        EmojiValidationUtils.validarSemEmoji(request.getNome(), "nome");
        EmojiValidationUtils.validarSemEmoji(request.getCpf(), "cpf");
        EmojiValidationUtils.validarSemEmoji(request.getEmail(), "email");
        EmojiValidationUtils.validarSemEmoji(request.getSenha(), "senha");

        if (request.getSenha() == null || request.getSenha().trim().isEmpty()) {
            throw new ValidacaoException("Senha e obrigatoria.");
        }

        if (request.getSenha().trim().length() <= 5) {
            throw new ValidacaoException("Senha deve ter mais de 5 caracteres.");
        }

        if (!request.getSenha().trim().matches(".*[A-Za-z].*")) {
            throw new ValidacaoException("Senha deve ter pelo menos uma letra.");
        }
    }
}
