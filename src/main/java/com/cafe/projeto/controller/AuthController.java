package com.cafe.projeto.controller;

import com.cafe.projeto.dto.AuthCadastroRequest;
import com.cafe.projeto.dto.CadastroResponse;
import com.cafe.projeto.service.AuthCadastroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthCadastroService authCadastroService;

    public AuthController(AuthCadastroService authCadastroService) {
        this.authCadastroService = authCadastroService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroResponse> cadastrar(@RequestBody AuthCadastroRequest request) {
        CadastroResponse response = authCadastroService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
