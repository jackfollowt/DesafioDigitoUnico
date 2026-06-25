package com.inter.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Recebe a chave publica (RSA, formato X.509/SPKI em Base64) usada para criptografar
 * os dados do usuario.
 */
public class ChavePublicaRequestDTO {

    @NotBlank(message = "chavePublica e obrigatoria")
    @Schema(description = "Chave publica RSA em Base64 (X.509/SPKI), com ou sem cabecalhos PEM")
    private String chavePublica;

    public String getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(String chavePublica) {
        this.chavePublica = chavePublica;
    }
}
