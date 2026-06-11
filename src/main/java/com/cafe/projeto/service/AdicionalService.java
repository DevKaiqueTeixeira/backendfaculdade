package com.cafe.projeto.service;

import com.cafe.projeto.dao.adicional.CadastrarAdicional;
import com.cafe.projeto.dao.adicional.ExcluirAdicional;
import com.cafe.projeto.dao.adicional.ListarAdicionaisPorProdutoId;
import com.cafe.projeto.dao.produto.ExisteProdutoPorId;
import com.cafe.projeto.dto.AdicionalCadastroRequest;
import com.cafe.projeto.dto.AdicionalResponse;
import com.cafe.projeto.dto.CadastroResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AdicionalService {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private final CadastrarAdicional cadastrarAdicional;
    private final ExcluirAdicional excluirAdicional;
    private final ExisteProdutoPorId existeProdutoPorId;
    private final ListarAdicionaisPorProdutoId listarAdicionaisPorProdutoId;
    private final SupabaseAdminAuthService supabaseAdminAuthService;

    public AdicionalService(
            CadastrarAdicional cadastrarAdicional,
            ExcluirAdicional excluirAdicional,
            ExisteProdutoPorId existeProdutoPorId,
            ListarAdicionaisPorProdutoId listarAdicionaisPorProdutoId,
            SupabaseAdminAuthService supabaseAdminAuthService
    ) {
        this.cadastrarAdicional = cadastrarAdicional;
        this.excluirAdicional = excluirAdicional;
        this.existeProdutoPorId = existeProdutoPorId;
        this.listarAdicionaisPorProdutoId = listarAdicionaisPorProdutoId;
        this.supabaseAdminAuthService = supabaseAdminAuthService;
    }

    public List<AdicionalResponse> listarPorProduto(Long produtoId) {
        if (produtoId == null || produtoId <= 0) {
            throw new ValidacaoException("Produto e obrigatorio.");
        }

        if (!existeProdutoPorId.executar(produtoId)) {
            throw new ValidacaoException("Produto nao encontrado.");
        }

        return listarAdicionaisPorProdutoId.executar(produtoId);
    }

    public CadastroResponse cadastrar(String authorization, AdicionalCadastroRequest request) {
        validarAdmin(authorization);
        validarRequest(request);
        normalizarRequest(request);

        if (!existeProdutoPorId.executar(request.getProdutoId())) {
            throw new ValidacaoException("Produto nao encontrado.");
        }

        Long id;

        try {
            id = cadastrarAdicional.executar(request);
        } catch (DataIntegrityViolationException ex) {
            throw new ValidacaoException("Nao foi possivel cadastrar o adicional.");
        }

        return new CadastroResponse(id, "Adicional cadastrado com sucesso.");
    }

    public CadastroResponse excluir(String authorization, Long id) {
        validarAdmin(authorization);

        if (id == null || id <= 0) {
            throw new ValidacaoException("Adicional invalido.");
        }

        try {
            Long adicionalId = excluirAdicional.executar(id);
            return new CadastroResponse(adicionalId, "Adicional excluido com sucesso.");
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidacaoException("Adicional nao encontrado.");
        }
    }

    private void validarAdmin(String authorization) {
        String email = supabaseAdminAuthService.buscarEmailPorAccessToken(extrairAccessToken(authorization));

        if (email == null || !ADMIN_EMAIL.equalsIgnoreCase(email.trim())) {
            throw new AutorizacaoException("Acesso permitido apenas para administrador.");
        }
    }

    private void validarRequest(AdicionalCadastroRequest request) {
        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            throw new ValidacaoException("Nome e obrigatorio.");
        }

        if (request.getNome().trim().length() > 120) {
            throw new ValidacaoException("Nome excede o tamanho permitido.");
        }

        EmojiValidationUtils.validarSemEmoji(request.getNome(), "nome do adicional");

        if (request.getPreco() == null) {
            throw new ValidacaoException("Preco e obrigatorio.");
        }

        if (request.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Preco deve ser maior que zero.");
        }

        if (request.getPreco().scale() > 2) {
            throw new ValidacaoException("Preco deve ter no maximo 2 casas decimais.");
        }

        if (request.getProdutoId() == null || request.getProdutoId() <= 0) {
            throw new ValidacaoException("Produto e obrigatorio.");
        }
    }

    private void normalizarRequest(AdicionalCadastroRequest request) {
        request.setNome(request.getNome().trim());
        request.setPreco(request.getPreco().setScale(2, RoundingMode.HALF_UP));
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
}
