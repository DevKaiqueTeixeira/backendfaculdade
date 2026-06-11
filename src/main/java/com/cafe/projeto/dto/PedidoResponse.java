package com.cafe.projeto.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private final Long id;
    private final Long clienteId;
    private final Long produtoId;
    private final String produtoNome;
    private final Long enderecoId;
    private final String enderecoResumo;
    private final BigDecimal precoProduto;
    private final BigDecimal valorTotal;
    private final String status;
    private final LocalDateTime criadoEm;
    private final List<PedidoAdicionalResponse> adicionais;

    public PedidoResponse(
            Long id,
            Long clienteId,
            Long produtoId,
            String produtoNome,
            Long enderecoId,
            String enderecoResumo,
            BigDecimal precoProduto,
            BigDecimal valorTotal,
            String status,
            LocalDateTime criadoEm,
            List<PedidoAdicionalResponse> adicionais
    ) {
        this.id = id;
        this.clienteId = clienteId;
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.enderecoId = enderecoId;
        this.enderecoResumo = enderecoResumo;
        this.precoProduto = precoProduto;
        this.valorTotal = valorTotal;
        this.status = status;
        this.criadoEm = criadoEm;
        this.adicionais = adicionais;
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public Long getEnderecoId() {
        return enderecoId;
    }

    public String getEnderecoResumo() {
        return enderecoResumo;
    }

    public BigDecimal getPrecoProduto() {
        return precoProduto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public List<PedidoAdicionalResponse> getAdicionais() {
        return adicionais;
    }
}
