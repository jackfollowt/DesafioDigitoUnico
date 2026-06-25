package com.inter.desafio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tratamento centralizado de erros, retornando respostas HTTP consistentes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoEncontrado(ResourceNotFoundException ex) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> tratarArgumentoInvalido(IllegalArgumentException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, Object> corpo = base(HttpStatus.BAD_REQUEST, "Erro de validacao");
        Map<String, String> campos = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }
        corpo.put("campos", campos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarGenerico(Exception ex) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> construir(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(base(status, mensagem));
    }

    private Map<String, Object> base(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now().toString());
        corpo.put("status", status.value());
        corpo.put("erro", status.getReasonPhrase());
        corpo.put("mensagem", mensagem);
        return corpo;
    }
}
