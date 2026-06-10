package com.cafe.projeto.service;

import com.cafe.projeto.command.AdicionarAdicionalCommand;
import com.cafe.projeto.command.PedidoCommander;
import com.cafe.projeto.dao.adicional.ListarAdicionaisPorProdutoId;
import com.cafe.projeto.dao.cliente.BuscarClientePorAuthUserId;
import com.cafe.projeto.dao.endereco.ExisteEnderecoDoCliente;
import com.cafe.projeto.dao.pedido.BuscarPedidoPorId;
import com.cafe.projeto.dao.pedido.CadastrarPedido;
import com.cafe.projeto.dao.pedido.CadastrarPedidoAdicional;
import com.cafe.projeto.dao.pedido.ListarAdicionaisPorPedidoId;
import com.cafe.projeto.dao.pedido.ListarPedidosPorClienteId;
import com.cafe.projeto.dao.produto.BuscarProdutoPorId;
import com.cafe.projeto.dto.AdicionalResponse;
import com.cafe.projeto.dto.EnderecoClienteConsulta;
import com.cafe.projeto.dto.PedidoAdicionalCadastroDados;
import com.cafe.projeto.dto.PedidoCadastroDados;
import com.cafe.projeto.dto.PedidoCadastroRequest;
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

    private final BuscarClientePorAuthUserId buscarClientePorAuthUserId;
    private final BuscarPedidoPorId buscarPedidoPorId;
    private final BuscarProdutoPorId buscarProdutoPorId;
    private final CadastrarPedido cadastrarPedido;
    private final CadastrarPedidoAdicional cadastrarPedidoAdicional;
    private final ExisteEnderecoDoCliente existeEnderecoDoCliente;
    private final ListarAdicionaisPorPedidoId listarAdicionaisPorPedidoId;
    private final ListarAdicionaisPorProdutoId listarAdicionaisPorProdutoId;
    private final ListarPedidosPorClienteId listarPedidosPorClienteId;
    private final PedidoCommander pedidoCommander;
    private final SupabaseAdminAuthService supabaseAdminAuthService;

    public PedidoService(
            BuscarClientePorAuthUserId buscarClientePorAuthUserId,
            BuscarPedidoPorId buscarPedidoPorId,
            BuscarProdutoPorId buscarProdutoPorId,
            CadastrarPedido cadastrarPedido,
            CadastrarPedidoAdicional cadastrarPedidoAdicional,
            ExisteEnderecoDoCliente existeEnderecoDoCliente,
            ListarAdicionaisPorPedidoId listarAdicionaisPorPedidoId,
            ListarAdicionaisPorProdutoId listarAdicionaisPorProdutoId,
            ListarPedidosPorClienteId listarPedidosPorClienteId,
            PedidoCommander pedidoCommander,
            SupabaseAdminAuthService supabaseAdminAuthService
    ) {
        this.buscarClientePorAuthUserId = buscarClientePorAuthUserId;
        this.buscarPedidoPorId = buscarPedidoPorId;
        this.buscarProdutoPorId = buscarProdutoPorId;
        this.cadastrarPedido = cadastrarPedido;
        this.cadastrarPedidoAdicional = cadastrarPedidoAdicional;
        this.existeEnderecoDoCliente = existeEnderecoDoCliente;
        this.listarAdicionaisPorPedidoId = listarAdicionaisPorPedidoId;
        this.listarAdicionaisPorProdutoId = listarAdicionaisPorProdutoId;
        this.listarPedidosPorClienteId = listarPedidosPorClienteId;
        this.pedidoCommander = pedidoCommander;
        this.supabaseAdminAuthService = supabaseAdminAuthService;
    }

    @Transactional
    public PedidoResponse confirmar(String authorization, PedidoCadastroRequest request) {
        Cliente cliente = buscarClienteAutenticado(authorization);
        validarRequest(request);
        validarEndereco(cliente.getId(), request.getEnderecoId());

        ProdutoResponse produto = buscarProduto(request.getProdutoId());
        List<AdicionalResponse> adicionais = buscarAdicionaisSelecionados(request, produto.getId());
        List<AdicionarAdicionalCommand> comandos = adicionais.stream()
                .map(AdicionarAdicionalCommand::new)
                .toList();

        BigDecimal valorTotal = pedidoCommander.executar(produto.getPreco(), comandos)
                .setScale(2, RoundingMode.HALF_UP);

        PedidoCadastroDados dados = new PedidoCadastroDados();
        dados.setClienteId(cliente.getId());
        dados.setProdutoId(produto.getId());
        dados.setEnderecoId(request.getEnderecoId());
        dados.setPrecoProduto(produto.getPreco().setScale(2, RoundingMode.HALF_UP));
        dados.setValorTotal(valorTotal);
        dados.setStatus(STATUS_CONFIRMADO);

        try {
            Long pedidoId = cadastrarPedido.executar(dados);

            for (AdicionalResponse adicional : adicionais) {
                cadastrarPedidoAdicional.executar(new PedidoAdicionalCadastroDados(
                        pedidoId,
                        adicional.getId(),
                        adicional.getPreco().setScale(2, RoundingMode.HALF_UP)
                ));
            }

            return preencherAdicionais(buscarPedidoPorId.executar(pedidoId));
        } catch (DataIntegrityViolationException ex) {
            throw new ValidacaoException("Nao foi possivel confirmar o pedido.");
        }
    }

    public List<PedidoResponse> listar(String authorization) {
        Cliente cliente = buscarClienteAutenticado(authorization);
        return listarPedidosPorClienteId.executar(cliente.getId()).stream()
                .map(this::preencherAdicionais)
                .toList();
    }

    private PedidoResponse preencherAdicionais(PedidoResponse pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getProdutoId(),
                pedido.getProdutoNome(),
                pedido.getEnderecoId(),
                pedido.getPrecoProduto(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.getCriadoEm(),
                listarAdicionaisPorPedidoId.executar(pedido.getId())
        );
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

    private void validarEndereco(Long clienteId, Long enderecoId) {
        if (enderecoId == null) {
            return;
        }

        if (!existeEnderecoDoCliente.executar(new EnderecoClienteConsulta(enderecoId, clienteId))) {
            throw new ValidacaoException("Endereco nao encontrado para o cliente.");
        }
    }

    private ProdutoResponse buscarProduto(Long produtoId) {
        try {
            return buscarProdutoPorId.executar(produtoId);
        } catch (EmptyResultDataAccessException ex) {
            throw new ValidacaoException("Produto nao encontrado.");
        }
    }

    private List<AdicionalResponse> buscarAdicionaisSelecionados(PedidoCadastroRequest request, Long produtoId) {
        List<Long> idsSelecionados = extrairIdsSelecionados(request);

        if (idsSelecionados.isEmpty()) {
            return List.of();
        }

        List<AdicionalResponse> adicionaisProduto = listarAdicionaisPorProdutoId.executar(produtoId);
        Map<Long, AdicionalResponse> adicionaisPorId = adicionaisProduto.stream()
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

    private List<Long> extrairIdsSelecionados(PedidoCadastroRequest request) {
        LinkedHashSet<Long> ids = new LinkedHashSet<>();

        if (request.getAdicionaisIds() != null) {
            ids.addAll(request.getAdicionaisIds());
        }

        if (request.getAdicionaisSelecionados() != null) {
            request.getAdicionaisSelecionados().entrySet().stream()
                    .filter(entry -> Boolean.TRUE.equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .forEach(ids::add);
        }

        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new ValidacaoException("Adicional invalido.");
            }
        }

        return new ArrayList<>(ids);
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
