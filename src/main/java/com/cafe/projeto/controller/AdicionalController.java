package com.cafe.projeto.controller;

import com.cafe.projeto.dto.AdicionalCadastroRequest;
import com.cafe.projeto.dto.AdicionalResponse;
import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.service.AdicionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/adicionais")
public class AdicionalController {

    private final AdicionalService adicionalService;

    public AdicionalController(AdicionalService adicionalService) {
        this.adicionalService = adicionalService;
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<AdicionalResponse>> listarPorProduto(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long produtoId
    ) {
        List<AdicionalResponse> response = adicionalService.listarPorProduto(authorization, produtoId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CadastroResponse> cadastrar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody AdicionalCadastroRequest request
    ) {
        CadastroResponse response = adicionalService.cadastrar(authorization, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CadastroResponse> excluir(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id
    ) {
        CadastroResponse response = adicionalService.excluir(authorization, id);
        return ResponseEntity.ok(response);
    }
}
