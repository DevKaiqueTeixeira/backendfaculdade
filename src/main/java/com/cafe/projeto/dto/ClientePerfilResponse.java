package com.cafe.projeto.dto;

import java.time.LocalDate;

public class ClientePerfilResponse {

    private final Long id;
    private final String nome;
    private final String cpf;
    private final String authUserId;
    private final String email;
    private final LocalDate dataNascimento;

    public ClientePerfilResponse(Long id, String nome, String cpf, String authUserId, String email, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.authUserId = authUserId;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getAuthUserId() {
        return authUserId;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
}
