package com.cafe.projeto.dto;

import java.util.List;
import java.util.Map;

public class PedidoCadastroRequest {

    private Long produtoId;
    private Long enderecoId;
    private List<Long> adicionaisIds;
    private Map<Long, Boolean> adicionaisSelecionados;

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

    public Map<Long, Boolean> getAdicionaisSelecionados() {
        return adicionaisSelecionados;
    }

    public void setAdicionaisSelecionados(Map<Long, Boolean> adicionaisSelecionados) {
        this.adicionaisSelecionados = adicionaisSelecionados;
    }
}
