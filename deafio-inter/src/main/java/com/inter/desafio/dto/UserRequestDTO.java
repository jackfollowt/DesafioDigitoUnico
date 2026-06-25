package com.inter.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para criacao/atualizacao de usuario.
 * Nome e email serao criptografados (RSA 2048) antes de persistir.
 */
public class UserRequestDTO {

    @NotBlank(message = "nome e obrigatorio")
    @Schema(example = "Maria Silva", description = "Nome do usuario (sera criptografado)")
    private String nome;

    @NotBlank(message = "email e obrigatorio")
    @Email(message = "email invalido")
    @Schema(example = "maria.silva@email.com", description = "Email do usuario (sera criptografado)")
    private String email;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
