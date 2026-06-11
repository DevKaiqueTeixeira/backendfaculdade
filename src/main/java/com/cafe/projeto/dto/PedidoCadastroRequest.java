package com.cafe.projeto.dto;

import java.util.List;

public class PedidoCadastroRequest {

    private Long produtoId;
    private Long enderecoId;
    private List<Long> adicionaisIds;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Long getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(Long enderecoId) {
        this.enderecoId = enderecoId;
    }

    public List<Long> getAdicionaisIds() {
        return adicionaisIds;
    }

    public void setAdicionaisIds(List<Long> adicionaisIds) {
        this.adicionaisIds = adicionaisIds;
    }
}
