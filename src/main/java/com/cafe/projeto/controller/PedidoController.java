package com.cafe.projeto.controller;

import com.cafe.projeto.dto.PedidoCadastroRequest;
import com.cafe.projeto.dto.PedidoResponse;
import com.cafe.projeto.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> confirmar(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody PedidoCadastroRequest request
    ) {
        PedidoResponse response = pedidoService.confirmar(authorization, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listar(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        List<PedidoResponse> response = pedidoService.listar(authorization);
        return ResponseEntity.ok(response);
    }
}
