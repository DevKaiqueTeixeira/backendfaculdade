package com.cafe.projeto.controller;

import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.ProdutoCadastroRequest;
import com.cafe.projeto.dto.ProdutoAtualizacaoRequest;
import com.cafe.projeto.dto.ProdutoResponse;
import com.cafe.projeto.service.ProdutoService;
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
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listar(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        List<ProdutoResponse> response = produtoService.listar(authorization);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CadastroResponse> cadastrar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody ProdutoCadastroRequest request
    ) {
        CadastroResponse response = produtoService.cadastrar(authorization, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CadastroResponse> atualizar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @RequestBody ProdutoAtualizacaoRequest request
    ) {
        CadastroResponse response = produtoService.atualizar(authorization, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CadastroResponse> excluir(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        CadastroResponse response = produtoService.excluir(authorization, id);
        return ResponseEntity.ok(response);
    }
}
