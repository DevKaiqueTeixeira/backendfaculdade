package com.cafe.projeto.controller;

import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.ClienteCadastroRequest;
import com.cafe.projeto.dto.ClienteAtualizacaoRequest;
import com.cafe.projeto.dto.ClientePerfilResponse;
import com.cafe.projeto.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<CadastroResponse> cadastrar(@RequestBody ClienteCadastroRequest request) {
        CadastroResponse response = clienteService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/perfil")
    public ResponseEntity<ClientePerfilResponse> buscarPerfil(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ClientePerfilResponse response = clienteService.buscarPerfil(authorization);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<CadastroResponse> atualizar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody ClienteAtualizacaoRequest request
    ) {
        CadastroResponse response = clienteService.atualizar(authorization, request);
        return ResponseEntity.ok(response);
    }
}
