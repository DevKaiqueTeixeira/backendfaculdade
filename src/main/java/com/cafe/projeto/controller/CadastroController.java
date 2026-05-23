package com.cafe.projeto.controller;

import com.cafe.projeto.model.Mensagem;
import com.cafe.projeto.repository.MensagemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class CadastroController {

    private final MensagemRepository mensagemRepository;

    public CadastroController(MensagemRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@RequestBody Mensagem novaMensagem) {
        mensagemRepository.save(novaMensagem);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novaMensagem.getMensagem() + " cadastrada com sucesso");
    }

    @GetMapping("/cadastro")
    public ResponseEntity<List<Mensagem>> listarTodos() {
        return ResponseEntity.ok(mensagemRepository.findAll());
    }
}
