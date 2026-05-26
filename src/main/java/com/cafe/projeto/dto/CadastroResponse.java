package com.cafe.projeto.dto;

public class CadastroResponse {

    private Long id;
    private String mensagem;

    public CadastroResponse(Long id, String mensagem) {
        this.id = id;
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public String getMensagem() {
        return mensagem;
    }
}
