package com.cafe.projeto.controller;

import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.EnderecoCadastroRequest;
import com.cafe.projeto.service.EnderecoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<CadastroResponse> cadastrar(@RequestBody EnderecoCadastroRequest request) {
        CadastroResponse response = enderecoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
