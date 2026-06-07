package com.cafe.projeto.controller;

import com.cafe.projeto.service.AutorizacaoException;
import com.cafe.projeto.service.ValidacaoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(ValidacaoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(AutorizacaoException.class)
    public ResponseEntity<Map<String, String>> handleAutorizacao(AutorizacaoException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("erro", ex.getMessage()));
    }
}
