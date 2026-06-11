package com.cafe.projeto.dto;

import java.time.LocalDateTime;

public class PedidoCadastroResultado {

    private final Long id;
    private final LocalDateTime criadoEm;

    public PedidoCadastroResultado(Long id, LocalDateTime criadoEm) {
        this.id = id;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
