package com.cafe.projeto.service;

import com.cafe.projeto.dao.cliente.BuscarClientePorAuthUserId;
import com.cafe.projeto.dao.endereco.AtualizarEndereco;
import com.cafe.projeto.dao.endereco.CadastrarEndereco;
import com.cafe.projeto.dao.endereco.ContarEnderecosPorClienteId;
import com.cafe.projeto.dao.endereco.ExcluirEndereco;
import com.cafe.projeto.dao.endereco.ListarEnderecosPorClienteId;
import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.EnderecoCadastroRequest;
import com.cafe.projeto.dto.EnderecoAtualizacaoRequest;
import com.cafe.projeto.dto.EnderecoResponse;
import com.cafe.projeto.model.Cliente;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EnderecoService {

    private final AtualizarEndereco atualizarEndereco;
    private final CadastrarEndereco cadastrarEndereco;
    private final ContarEnderecosPorClienteId contarEnderecosPorClienteId;
    private final ExcluirEndereco excluirEndereco;
    private final ListarEnderecosPorClienteId listarEnderecosPorClienteId;
    private final BuscarClientePorAuthUserId buscarClientePorAuthUserId;
    private final SupabaseAdminAuthService supabaseAdminAuthService;

    public EnderecoService(
            AtualizarEndereco atualizarEndereco,
            CadastrarEndereco cadastrarEndereco,
            ContarEnderecosPorClienteId contarEnderecosPorClienteId,
            ExcluirEndereco excluirEndereco,
            ListarEnderecosPorClienteId listarEnderecosPorClienteId,
            BuscarClientePorAuthUserId buscarClientePorAuthUserId,
            SupabaseAdminAuthService supabaseAdminAuthService
    ) {
        this.atualizarEndereco = atualizarEndereco;
        this.cadastrarEndereco = cadastrarEndereco;
        this.contarEnderecosPorClienteId = contarEnderecosPorClienteId;
        this.excluirEndereco = excluirEndereco;
        this.listarEnderecosPorClienteId = listarEnderecosPorClienteId;
        this.buscarClientePorAuthUserId = buscarClientePorAuthUserId;
        this.supabaseAdminAuthService = supabaseAdminAuthService;
    }

    public List<EnderecoResponse> listar(String authorization) {
        Cliente cliente = buscarClienteAutenticado(authorization);
        return listarEnderecosPorClienteId.executar(cliente.getId());
    }

    public CadastroResponse cadastrar(String authorization, EnderecoCadastroRequest request) {
        Cliente cliente = buscarClienteAutenticado(authorization);

        if (contarEnderecosPorClienteId.executar(cliente.getId()) >= 2) {
            throw new ValidacaoException("Limite de 2 enderecos atingido.");
        }

        prepararEnderecoRequest(request, cliente.getId());

        Long id;

        try {
            id = cadastrarEndereco.executar(request);
        } catch (DataIntegrityViolationException ex) {
            throw new ValidacaoException("Nao foi possivel salvar o endereco informado.");
        }

        return new CadastroResponse(id, "Endereco cadastrado com sucesso.");
    }

    public CadastroResponse atualizar(String authorization, Long id, EnderecoAtualizacaoRequest request) {
        Cliente cliente = buscarClienteAutenticado(authorization);

        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        request.setId(id);
        prepararEnderecoRequest(request, cliente.getId());

        try {
            Long enderecoId = atualizarEndereco.executar(request);
            return new CadastroResponse(enderecoId, "Endereco atualizado com sucesso.");
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidacaoException("Endereco nao encontrado.");
        } catch (DataIntegrityViolationException ex) {
            throw new ValidacaoException("Nao foi possivel salvar o endereco informado.");
        }
    }

    public CadastroResponse excluir(String authorization, Long id) {
        Cliente cliente = buscarClienteAutenticado(authorization);
        EnderecoAtualizacaoRequest request = new EnderecoAtualizacaoRequest();
        request.setId(id);
        request.setClienteId(cliente.getId());

        try {
            Long enderecoId = excluirEndereco.executar(request);
            return new CadastroResponse(enderecoId, "Endereco excluido com sucesso.");
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidacaoException("Endereco nao encontrado.");
        }
    }

    private void validarRequest(EnderecoCadastroRequest request) {
        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        validarCampoObrigatorio(request.getCep(), "CEP");
        validarCampoObrigatorio(request.getLogradouro(), "Logradouro");
        validarCampoObrigatorio(request.getNumero(), "Numero");
        validarCampoObrigatorio(request.getBairro(), "Bairro");
        validarCampoObrigatorio(request.getCidade(), "Cidade");
        validarCampoObrigatorio(request.getEstado(), "Estado");
        validarCampoObrigatorio(request.getPais(), "Pais");
        validarCampoObrigatorio(request.getTipoEndereco(), "TipoEndereco");
        validarTamanhoMaximo(request.getCep(), 9, "CEP");
        validarTamanhoMaximo(request.getLogradouro(), 160, "Logradouro");
        validarTamanhoMaximo(request.getNumero(), 20, "Numero");
        validarTamanhoMaximo(request.getComplemento(), 120, "Complemento");
        validarTamanhoMaximo(request.getBairro(), 120, "Bairro");
        validarTamanhoMaximo(request.getCidade(), 120, "Cidade");
        validarTamanhoMaximo(request.getEstado(), 2, "Estado");
        validarTamanhoMaximo(request.getPais(), 80, "Pais");
        validarTamanhoMaximo(request.getPontoReferencia(), 160, "PontoReferencia");
        validarTamanhoMaximo(request.getTipoEndereco(), 40, "TipoEndereco");

        if (normalizarCep(request.getCep()).length() != 8) {
            throw new ValidacaoException("CEP invalido.");
        }

        if (request.getEstado().trim().length() != 2) {
            throw new ValidacaoException("Estado invalido.");
        }
    }

    private void prepararEnderecoRequest(EnderecoCadastroRequest request, Long clienteId) {
        validarRequest(request);
        request.setClienteId(clienteId);
        request.setCep(normalizarCep(request.getCep()));
        request.setLogradouro(request.getLogradouro().trim());
        request.setNumero(request.getNumero().trim());
        request.setBairro(request.getBairro().trim());
        request.setCidade(request.getCidade().trim());
        request.setEstado(request.getEstado().trim().toUpperCase());
        request.setPais(request.getPais().trim());
        request.setTipoEndereco(request.getTipoEndereco().trim());

        if (request.getComplemento() != null) {
            request.setComplemento(request.getComplemento().trim());
        }

        if (request.getPontoReferencia() != null) {
            request.setPontoReferencia(request.getPontoReferencia().trim());
        }
    }

    private void validarCampoObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException(campo + " e obrigatorio.");
        }
    }

    private void validarTamanhoMaximo(String valor, int tamanhoMaximo, String campo) {
        if (valor != null && valor.trim().length() > tamanhoMaximo) {
            throw new ValidacaoException(campo + " excede o tamanho permitido.");
        }
    }

    private Cliente buscarClienteAutenticado(String authorization) {
        String authUserId = supabaseAdminAuthService.buscarUsuarioIdPorAccessToken(extrairAccessToken(authorization));
        Cliente cliente = buscarClientePorAuthUserId.executar(UUID.fromString(authUserId));

        if (cliente == null) {
            throw new ValidacaoException("Cliente nao encontrado.");
        }

        return cliente;
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

    private String normalizarCep(String cep) {
        return cep.replaceAll("\\D", "");
    }
}
