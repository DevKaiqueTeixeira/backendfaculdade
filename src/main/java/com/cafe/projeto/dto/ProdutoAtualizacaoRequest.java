package com.cafe.projeto.dto;

public class ProdutoAtualizacaoRequest extends ProdutoCadastroRequest {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
