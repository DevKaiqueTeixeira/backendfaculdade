package com.cafe.projeto.service;

import com.cafe.projeto.dao.cliente.ExisteClientePorId;
import com.cafe.projeto.dao.endereco.CadastrarEndereco;
import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.EnderecoCadastroRequest;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    private final CadastrarEndereco cadastrarEndereco;
    private final ExisteClientePorId existeClientePorId;

    public EnderecoService(
            CadastrarEndereco cadastrarEndereco,
            ExisteClientePorId existeClientePorId
    ) {
        this.cadastrarEndereco = cadastrarEndereco;
        this.existeClientePorId = existeClientePorId;
    }

    public CadastroResponse cadastrar(EnderecoCadastroRequest request) {
        validarRequest(request);

        request.setCep(request.getCep().trim());
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

        Long id = cadastrarEndereco.executar(request);
        return new CadastroResponse(id, "Endereco cadastrado com sucesso.");
    }

    private void validarRequest(EnderecoCadastroRequest request) {
        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        if (request.getClienteId() == null) {
            throw new ValidacaoException("clienteId e obrigatorio.");
        }

        if (!existeClientePorId.executar(request.getClienteId())) {
            throw new ValidacaoException("Cliente nao encontrado.");
        }

        validarCampoObrigatorio(request.getCep(), "CEP");
        validarCampoObrigatorio(request.getLogradouro(), "Logradouro");
        validarCampoObrigatorio(request.getNumero(), "Numero");
        validarCampoObrigatorio(request.getBairro(), "Bairro");
        validarCampoObrigatorio(request.getCidade(), "Cidade");
        validarCampoObrigatorio(request.getEstado(), "Estado");
        validarCampoObrigatorio(request.getPais(), "Pais");
        validarCampoObrigatorio(request.getTipoEndereco(), "TipoEndereco");
    }

    private void validarCampoObrigatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacaoException(campo + " e obrigatorio.");
        }
    }
}
