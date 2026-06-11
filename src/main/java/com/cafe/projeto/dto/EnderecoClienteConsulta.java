package com.cafe.projeto.dto;

public class EnderecoClienteConsulta {

    private final Long enderecoId;
    private final Long clienteId;

    public EnderecoClienteConsulta(Long enderecoId, Long clienteId) {
        this.enderecoId = enderecoId;
        this.clienteId = clienteId;
    }

    public Long getEnderecoId() {
        return enderecoId;
    }

    public Long getClienteId() {
        return clienteId;
    }
}
