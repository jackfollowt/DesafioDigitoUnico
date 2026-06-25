package com.inter.desafio.controller;

import com.inter.desafio.config.KeyManager;
import com.inter.desafio.dto.ChavePublicaRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint para envio da chave publica usada na criptografia dos dados de usuario.
 */
@RestController
@RequestMapping("/api/chave-publica")
@Tag(name = "Chave Publica", description = "Gestao da chave publica RSA para criptografia")
public class ChavePublicaController {

    private final KeyManager keyManager;

    public ChavePublicaController(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    @PostMapping
    @Operation(summary = "Recebe a chave publica do cliente (Base64 X.509/SPKI) para criptografar os dados")
    public Map<String, String> enviar(@Valid @RequestBody ChavePublicaRequestDTO dto) {
        keyManager.definirChavePublica(dto.getChavePublica());
        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put("mensagem", "Chave publica registrada com sucesso. Novos dados serao criptografados com ela.");
        return resposta;
    }

    @GetMapping("/servidor")
    @Operation(summary = "Retorna a chave publica do servidor (util para testes do fluxo de criptografia)")
    public Map<String, String> chaveDoServidor() {
        Map<String, String> resposta = new LinkedHashMap<>();
        resposta.put("chavePublicaServidor", keyManager.getChavePublicaServidorBase64());
        resposta.put("chavePublicaAtiva", keyManager.getChavePublicaAtivaBase64());
        return resposta;
    }
}
