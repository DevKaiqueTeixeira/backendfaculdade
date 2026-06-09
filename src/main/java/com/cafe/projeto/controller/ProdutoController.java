package com.cafe.projeto.controller;

import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.dto.ProdutoCadastroRequest;
import com.cafe.projeto.dto.ProdutoAtualizacaoRequest;
import com.cafe.projeto.dto.ProdutoResponse;
import com.cafe.projeto.service.ProdutoService;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CadastroResponse> cadastrar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam String nome,
            @RequestParam java.math.BigDecimal preco,
            @RequestPart("imagem") MultipartFile imagem
    ) {
        ProdutoCadastroRequest request = new ProdutoCadastroRequest();
        request.setNome(nome);
        request.setPreco(preco);

        CadastroResponse response = produtoService.cadastrar(authorization, request, imagem);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CadastroResponse> atualizar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @RequestParam String nome,
            @RequestParam java.math.BigDecimal preco,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem
    ) {
        ProdutoAtualizacaoRequest request = new ProdutoAtualizacaoRequest();
        request.setNome(nome);
        request.setPreco(preco);

        CadastroResponse response = produtoService.atualizar(authorization, id, request, imagem);
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
