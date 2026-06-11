package com.cafe.projeto.service;

import com.cafe.projeto.dao.adicional.ListarAdicionaisPorProdutoId;
import com.cafe.projeto.dao.cliente.BuscarClientePorAuthUserId;
import com.cafe.projeto.dao.endereco.BuscarEnderecoDoCliente;
import com.cafe.projeto.dao.pedido.CadastrarPedido;
import com.cafe.projeto.dao.pedido.CadastrarPedidoAdicional;
import com.cafe.projeto.dao.pedido.ListarAdicionaisPorPedidoIds;
import com.cafe.projeto.dao.pedido.ListarPedidosPorClienteId;
import com.cafe.projeto.dao.produto.BuscarProdutoPorId;
import com.cafe.projeto.decorator.Pedido;
import com.cafe.projeto.decorator.PedidoBase;
import com.cafe.projeto.decorator.PedidoComAdicional;
import com.cafe.projeto.dto.AdicionalResponse;
import com.cafe.projeto.dto.EnderecoClienteConsulta;
import com.cafe.projeto.dto.EnderecoResponse;
import com.cafe.projeto.dto.PedidoAdicionalCadastroDados;
import com.cafe.projeto.dto.PedidoAdicionalResponse;
import com.cafe.projeto.dto.PedidoCadastroDados;
import com.cafe.projeto.dto.PedidoCadastroRequest;
import com.cafe.projeto.dto.PedidoCadastroResultado;
import com.cafe.projeto.dto.PedidoResponse;
import com.cafe.projeto.dto.ProdutoResponse;
import com.cafe.projeto.model.Cliente;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private static final String STATUS_CONFIRMADO = "CONFIRMADO";

    private final ListarAdicionaisPorPedidoIds listarAdicionaisPorPedidoIds;
    private final BuscarClientePorAuthUserId buscarClientePorAuthUserId;
    private final BuscarEnderecoDoCliente buscarEnderecoDoCliente;
    private final BuscarProdutoPorId buscarProdutoPorId;
    private final CadastrarPedido cadastrarPedido;
    private final CadastrarPedidoAdicional cadastrarPedidoAdicional;
    private final ListarAdicionaisPorProdutoId listarAdicionaisPorProdutoId;
    private final ListarPedidosPorClienteId listarPedidosPorClienteId;
    private final SupabaseAdminAuthService supabaseAdminAuthService;

    public PedidoService(
            ListarAdicionaisPorPedidoIds listarAdicionaisPorPedidoIds,
            BuscarClientePorAuthUserId buscarClientePorAuthUserId,
            BuscarEnderecoDoCliente buscarEnderecoDoCliente,
            BuscarProdutoPorId buscarProdutoPorId,
            CadastrarPedido cadastrarPedido,
            CadastrarPedidoAdicional cadastrarPedidoAdicional,
            ListarAdicionaisPorProdutoId listarAdicionaisPorProdutoId,
            ListarPedidosPorClienteId listarPedidosPorClienteId,
            SupabaseAdminAuthService supabaseAdminAuthService
    ) {
        this.listarAdicionaisPorPedidoIds = listarAdicionaisPorPedidoIds;
        this.buscarClientePorAuthUserId = buscarClientePorAuthUserId;
        this.buscarEnderecoDoCliente = buscarEnderecoDoCliente;
        this.buscarProdutoPorId = buscarProdutoPorId;
        this.cadastrarPedido = cadastrarPedido;
        this.cadastrarPedidoAdicional = cadastrarPedidoAdicional;
        this.listarAdicionaisPorProdutoId = listarAdicionaisPorProdutoId;
        this.listarPedidosPorClienteId = listarPedidosPorClienteId;
        this.supabaseAdminAuthService = supabaseAdminAuthService;
    }

    @Transactional
    public PedidoResponse confirmar(String authorization, PedidoCadastroRequest request) {
        Cliente cliente = buscarClienteAutenticado(authorization);
        validarRequest(request);

        ProdutoResponse produto = buscarProduto(request.getProdutoId());
        EnderecoResponse endereco = buscarEndereco(cliente.getId(), request.getEnderecoId());
        List<AdicionalResponse> adicionaisSelecionados = buscarAdicionaisSelecionados(request.getAdicionaisIds(), produto.getId());

        Pedido pedido = montarPedido(produto, adicionaisSelecionados);
        BigDecimal precoProduto = normalizarValor(produto.getPreco());
        BigDecimal valorTotal = normalizarValor(pedido.calcularValorTotal());
        List<PedidoAdicionalResponse> adicionais = normalizarAdicionais(pedido.listarAdicionais());

        PedidoCadastroDados dados = new PedidoCadastroDados();
        dados.setClienteId(cliente.getId());
        dados.setProdutoId(produto.getId());
        dados.setProdutoNome(produto.getNome());
        dados.setEnderecoId(endereco != null ? endereco.getId() : null);
        dados.setEnderecoResumo(formatarEndereco(endereco));
        dados.setPrecoProduto(precoProduto);
        dados.setValorTotal(valorTotal);
        dados.setStatus(STATUS_CONFIRMADO);

        try {
            PedidoCadastroResultado cadastro = cadastrarPedido.executar(dados);

            for (PedidoAdicionalResponse adicional : adicionais) {
                cadastrarPedidoAdicional.executar(new PedidoAdicionalCadastroDados(
                        cadastro.getId(),
                        adicional.getAdicionalId(),
                        adicional.getNome(),
                        adicional.getPreco()
                ));
            }

            return new PedidoResponse(
                    cadastro.getId(),
                    cliente.getId(),
                    produto.getId(),
                    produto.getNome(),
                    endereco != null ? endereco.getId() : null,
                    formatarEndereco(endereco),
                    precoProduto,
                    valorTotal,
                    STATUS_CONFIRMADO,
                    cadastro.getCriadoEm(),
                    adicionais
            );
        } catch (DataIntegrityViolationException ex) {
            throw new ValidacaoException("Nao foi possivel confirmar o pedido.");
        }
    }

    public List<PedidoResponse> listar(String authorization) {
        Cliente cliente = buscarClienteAutenticado(authorization);
        List<PedidoResponse> pedidos = listarPedidosPorClienteId.executar(cliente.getId());

        if (pedidos.isEmpty()) {
            return pedidos;
        }

        List<Long> pedidoIds = pedidos.stream().map(PedidoResponse::getId).toList();
        Map<Long, List<PedidoAdicionalResponse>> adicionaisPorPedido = listarAdicionaisPorPedidoIds.executar(pedidoIds);

        return pedidos.stream()
                .map(pedido -> new PedidoResponse(
                        pedido.getId(),
                        pedido.getClienteId(),
                        pedido.getProdutoId(),
                        pedido.getProdutoNome(),
                        pedido.getEnderecoId(),
                        pedido.getEnderecoResumo(),
                        pedido.getPrecoProduto(),
                        pedido.getValorTotal(),
                        pedido.getStatus(),
                        pedido.getCriadoEm(),
                        adicionaisPorPedido.getOrDefault(pedido.getId(), List.of())
                ))
                .toList();
    }

    private void validarRequest(PedidoCadastroRequest request) {
        if (request == null) {
            throw new ValidacaoException("Body da requisicao e obrigatorio.");
        }

        if (request.getProdutoId() == null || request.getProdutoId() <= 0) {
            throw new ValidacaoException("Produto e obrigatorio.");
        }

        if (request.getEnderecoId() != null && request.getEnderecoId() <= 0) {
            throw new ValidacaoException("Endereco invalido.");
        }
    }

    private ProdutoResponse buscarProduto(Long produtoId) {
        try {
            return buscarProdutoPorId.executar(produtoId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidacaoException("Produto nao encontrado.");
        }
    }

    private EnderecoResponse buscarEndereco(Long clienteId, Long enderecoId) {
        if (enderecoId == null) {
            return null;
        }

        EnderecoResponse endereco = buscarEnderecoDoCliente.executar(new EnderecoClienteConsulta(enderecoId, clienteId));

        if (endereco == null) {
            throw new ValidacaoException("Endereco nao encontrado para o cliente.");
        }

        return endereco;
    }

    private List<AdicionalResponse> buscarAdicionaisSelecionados(List<Long> adicionaisIds, Long produtoId) {
        List<Long> idsSelecionados = extrairIdsSelecionados(adicionaisIds);

        if (idsSelecionados.isEmpty()) {
            return List.of();
        }

        Map<Long, AdicionalResponse> adicionaisPorId = listarAdicionaisPorProdutoId.executar(produtoId).stream()
                .collect(Collectors.toMap(AdicionalResponse::getId, Function.identity()));

        List<AdicionalResponse> adicionaisSelecionados = new ArrayList<>();

        for (Long adicionalId : idsSelecionados) {
            AdicionalResponse adicional = adicionaisPorId.get(adicionalId);

            if (adicional == null) {
                throw new ValidacaoException("Adicional invalido para o produto selecionado.");
            }

            adicionaisSelecionados.add(adicional);
        }

        return adicionaisSelecionados;
    }

    private List<Long> extrairIdsSelecionados(List<Long> adicionaisIds) {
        if (adicionaisIds == null || adicionaisIds.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<Long> ids = new LinkedHashSet<>(adicionaisIds);

        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new ValidacaoException("Adicional invalido.");
            }
        }

        return new ArrayList<>(ids);
    }

    private Pedido montarPedido(ProdutoResponse produto, List<AdicionalResponse> adicionaisSelecionados) {
        Pedido pedido = new PedidoBase(produto.getPreco());

        for (AdicionalResponse adicional : adicionaisSelecionados) {
            pedido = new PedidoComAdicional(pedido, new PedidoAdicionalResponse(
                    adicional.getId(),
                    adicional.getNome(),
                    normalizarValor(adicional.getPreco())
            ));
        }

        return pedido;
    }

    private List<PedidoAdicionalResponse> normalizarAdicionais(List<PedidoAdicionalResponse> adicionais) {
        return adicionais.stream()
                .map(adicional -> new PedidoAdicionalResponse(
                        adicional.getAdicionalId(),
                        adicional.getNome(),
                        normalizarValor(adicional.getPreco())
                ))
                .toList();
    }

    private BigDecimal normalizarValor(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private String formatarEndereco(EnderecoResponse endereco) {
        if (endereco == null) {
            return null;
        }

        StringBuilder resumo = new StringBuilder();
        resumo.append(endereco.getTipoEndereco())
                .append(": ")
                .append(endereco.getLogradouro())
                .append(", ")
                .append(endereco.getNumero());

        if (endereco.getComplemento() != null && !endereco.getComplemento().isBlank()) {
            resumo.append(" - ").append(endereco.getComplemento());
        }

        resumo.append(" | ")
                .append(endereco.getBairro())
                .append(" - ")
                .append(endereco.getCidade())
                .append("/")
                .append(endereco.getEstado());

        return resumo.toString();
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
}
