package com.cafe.projeto.service;

import com.cafe.projeto.dao.produto.AtualizarProduto;
import com.cafe.projeto.dao.produto.BuscarProdutoPorId;
import com.cafe.projeto.dao.produto.CadastrarProduto;
import com.cafe.projeto.dao.produto.ExcluirProduto;
import com.cafe.projeto.dao.produto.ListarProdutos;
import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.ProdutoCadastroRequest;
import com.cafe.projeto.dto.ProdutoAtualizacaoRequest;
import com.cafe.projeto.dto.ProdutoResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ProdutoService {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private final AtualizarProduto atualizarProduto;
    private final BuscarProdutoPorId buscarProdutoPorId;
    private final CadastrarProduto cadastrarProduto;
    private final ExcluirProduto excluirProduto;
    private final ListarProdutos listarProdutos;
    private final ProdutoImagemStorageService produtoImagemStorageService;
    private final SupabaseAdminAuthService supabaseAdminAuthService;

    public ProdutoService(
            AtualizarProduto atualizarProduto,
            BuscarProdutoPorId buscarProdutoPorId,
            CadastrarProduto cadastrarProduto,
            ExcluirProduto excluirProduto,
            ListarProdutos listarProdutos,
            ProdutoImagemStorageService produtoImagemStorageService,
            SupabaseAdminAuthService supabaseAdminAuthService
    ) {
        this.atualizarProduto = atualizarProduto;
        this.buscarProdutoPorId = buscarProdutoPorId;
        this.cadastrarProduto = cadastrarProduto;
        this.excluirProduto = excluirProduto;
        this.listarProdutos = listarProdutos;
        this.produtoImagemStorageService = produtoImagemStorageService;
        this.supabaseAdminAuthService = supabaseAdminAuthService;
    }

    public List<ProdutoResponse> listar(String authorization) {
        return listarProdutos.executar(null);
    }

    public CadastroResponse cadastrar(String authorization, ProdutoCadastroRequest request, MultipartFile imagem) {
        validarAdmin(authorization);
        validarRequest(request);
        normalizarRequest(request);
        request.setImagemUrl(produtoImagemStorageService.salvar(imagem));

        Long id;

        try {
            id = cadastrarProduto.executar(request);
        } catch (DataIntegrityViolationException ex) {
            produtoImagemStorageService.excluir(request.getImagemUrl());
            throw new ValidacaoException("Nao foi possivel cadastrar o produto.");
        }

        return new CadastroResponse(id, "Produto cadastrado com sucesso.");
    }

    public CadastroResponse atualizar(String authorization, Long id, ProdutoAtualizacaoRequest request, MultipartFile imagem) {
        validarAdmin(authorization);

        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        ProdutoResponse produtoAtual;

        try {
            produtoAtual = buscarProdutoPorId.executar(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidacaoException("Produto nao encontrado.");
        }

        request.setId(id);
        validarRequest(request);
        normalizarRequest(request);

        String imagemUrlAnterior = produtoAtual.getImagemUrl();
        String novaImagemUrl = imagemUrlAnterior;

        if (imagem != null && !imagem.isEmpty()) {
            novaImagemUrl = produtoImagemStorageService.salvar(imagem);
        }

        request.setImagemUrl(novaImagemUrl);

        try {
            Long produtoId = atualizarProduto.executar(request);

            if (imagem != null && !imagem.isEmpty() && imagemUrlAnterior != null && !imagemUrlAnterior.equals(novaImagemUrl)) {
                produtoImagemStorageService.excluir(imagemUrlAnterior);
            }

            return new CadastroResponse(produtoId, "Produto atualizado com sucesso.");
        } catch (EmptyResultDataAccessException ex) {
            if (imagem != null && !imagem.isEmpty() && novaImagemUrl != null && !novaImagemUrl.equals(imagemUrlAnterior)) {
                produtoImagemStorageService.excluir(novaImagemUrl);
            }
            throw new ValidacaoException("Produto nao encontrado.");
        } catch (DataIntegrityViolationException ex) {
            if (imagem != null && !imagem.isEmpty() && novaImagemUrl != null && !novaImagemUrl.equals(imagemUrlAnterior)) {
                produtoImagemStorageService.excluir(novaImagemUrl);
            }
            throw new ValidacaoException("Nao foi possivel atualizar o produto.");
        }
    }

    public CadastroResponse excluir(String authorization, Long id) {
        validarAdmin(authorization);

        try {
            ProdutoResponse produto = buscarProdutoPorId.executar(id);
            Long produtoId = excluirProduto.executar(id);
            produtoImagemStorageService.excluir(produto.getImagemUrl());
            return new CadastroResponse(produtoId, "Produto excluido com sucesso.");
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidacaoException("Produto nao encontrado.");
        }
    }

    private void validarAdmin(String authorization) {
        String email = supabaseAdminAuthService.buscarEmailPorAccessToken(extrairAccessToken(authorization));

        if (email == null || !ADMIN_EMAIL.equalsIgnoreCase(email.trim())) {
            throw new AutorizacaoException("Acesso permitido apenas para administrador.");
        }
    }

    private void validarRequest(ProdutoCadastroRequest request) {
        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        if (request.getNome() == null || request.getNome().trim().isEmpty()) {
            throw new ValidacaoException("Nome e obrigatorio.");
        }

        if (request.getNome().trim().length() > 120) {
            throw new ValidacaoException("Nome excede o tamanho permitido.");
        }

        EmojiValidationUtils.validarSemEmoji(request.getNome(), "nome do produto");

        if (request.getPreco() == null) {
            throw new ValidacaoException("Preco e obrigatorio.");
        }

        if (request.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacaoException("Preco deve ser maior que zero.");
        }

        if (request.getPreco().scale() > 2) {
            throw new ValidacaoException("Preco deve ter no maximo 2 casas decimais.");
        }

        if (request.getImagemUrl() != null && request.getImagemUrl().length() > 255) {
            throw new ValidacaoException("Caminho da imagem excede o tamanho permitido.");
        }
    }

    private void normalizarRequest(ProdutoCadastroRequest request) {
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
