package com.cafe.projeto.dto;

public class EnderecoResponse {

    private final Long id;
    private final String cep;
    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final String pais;
    private final String pontoReferencia;
    private final String tipoEndereco;

    public EnderecoResponse(
            Long id,
            String cep,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cidade,
            String estado,
            String pais,
            String pontoReferencia,
            String tipoEndereco
    ) {
        this.id = id;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.pais = pais;
        this.pontoReferencia = pontoReferencia;
        this.tipoEndereco = tipoEndereco;
    }

    public Long getId() {
        return id;
    }

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getPais() {
        return pais;
    }

    public String getPontoReferencia() {
        return pontoReferencia;
    }

    public String getTipoEndereco() {
        return tipoEndereco;
    }
}
