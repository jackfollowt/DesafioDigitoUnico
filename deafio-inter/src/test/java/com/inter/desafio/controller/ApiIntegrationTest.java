package com.inter.desafio.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inter.desafio.config.KeyManager;
import com.inter.desafio.service.CryptoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integracao da API (camada web + servicos + H2).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private KeyManager keyManager;

    @Test
    @DisplayName("POST /api/digito-unico calcula o exemplo da especificacao (n=9875, k=4 -> 8)")
    void calculaDigitoUnico() throws Exception {
        mockMvc.perform(post("/api/digito-unico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"n\":\"9875\",\"k\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value(8));
    }

    @Test
    @DisplayName("Segundo calculo identico vem do cache")
    void segundoCalculoVemDoCache() throws Exception {
        String body = "{\"n\":\"123456\",\"k\":7}";
        mockMvc.perform(post("/api/digito-unico").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/digito-unico").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origemCache").value(true));
    }

    @Test
    @DisplayName("Validacao: n nao numerico retorna 400")
    void validaEntrada() throws Exception {
        mockMvc.perform(post("/api/digito-unico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"n\":\"12a3\",\"k\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Fluxo CRUD + criptografia: cria usuario, dado e cifrado e o servidor descriptografa")
    void crudComCriptografia() throws Exception {
        String body = "{\"nome\":\"Maria Silva\",\"email\":\"maria.silva@email.com\"}";
        MvcResult result = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.criptografado").value(false))
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        long id = node.get("id").asLong();

        // associa um calculo ao usuario
        mockMvc.perform(post("/api/digito-unico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"n\":\"9875\",\"k\":4,\"usuarioId\":" + id + "}"))
                .andExpect(status().isOk());

        // recupera os calculos do usuario
        mockMvc.perform(get("/api/usuarios/" + id + "/calculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultado").value(8));

        // deleta
        mockMvc.perform(delete("/api/usuarios/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/usuarios/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("CryptoService realmente cifra: o nome armazenado nao e texto plano")
    void dadoArmazenadoEstaCifrado() {
        // confirma que o que e persistido difere do texto original e e decifravel pelo servidor
        String cifrado = cryptoService.criptografar("Joao", keyManager.getChavePublicaAtiva());
        assertNotEquals("Joao", cifrado);
        assertEquals("Joao", cryptoService.descriptografar(cifrado, keyManager.getChavePrivadaServidor()));
    }

    @Test
    @DisplayName("POST /api/chave-publica aceita chave externa; servidor passa a nao descriptografar")
    void enviaChavePublicaExterna() throws Exception {
        KeyPair clienteExterno = cryptoService.gerarParDeChaves();
        String pubBase64 = cryptoService.paraBase64(clienteExterno.getPublic());

        mockMvc.perform(post("/api/chave-publica")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chavePublica\":\"" + pubBase64 + "\"}"))
                .andExpect(status().isOk());

        // usuario criado agora e cifrado com a chave do cliente -> servidor nao descriptografa
        MvcResult result = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Cliente Externo\",\"email\":\"ext@email.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.criptografado").value(true))
                .andReturn();

        // mas o cliente, com sua chave privada, consegue descriptografar o ciphertext retornado
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        String nomeCifrado = node.get("nome").asText();
        String decifrado = cryptoService.descriptografar(nomeCifrado, clienteExterno.getPrivate());
        assertEquals("Cliente Externo", decifrado);

        // restaura a chave do servidor para nao afetar outros testes
        keyManager.inicializar();
    }
}
