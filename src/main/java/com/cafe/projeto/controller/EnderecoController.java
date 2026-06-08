package com.cafe.projeto.controller;

import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.EnderecoCadastroRequest;
import com.cafe.projeto.dto.EnderecoAtualizacaoRequest;
import com.cafe.projeto.dto.EnderecoResponse;
import com.cafe.projeto.service.EnderecoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping
    public ResponseEntity<List<EnderecoResponse>> listar(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        List<EnderecoResponse> response = enderecoService.listar(authorization);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CadastroResponse> cadastrar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody EnderecoCadastroRequest request
    ) {
        CadastroResponse response = enderecoService.cadastrar(authorization, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CadastroResponse> atualizar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @RequestBody EnderecoAtualizacaoRequest request
    ) {
        CadastroResponse response = enderecoService.atualizar(authorization, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CadastroResponse> excluir(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        CadastroResponse response = enderecoService.excluir(authorization, id);
        return ResponseEntity.ok(response);
    }
}
