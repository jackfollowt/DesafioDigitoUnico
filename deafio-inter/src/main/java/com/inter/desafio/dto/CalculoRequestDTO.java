package com.inter.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Parametros de entrada para o calculo do digito unico.
 * A associacao a um usuario (usuarioId) e opcional.
 */
public class CalculoRequestDTO {

    @NotBlank(message = "n e obrigatorio")
    @Pattern(regexp = "\\d{1,1000001}", message = "n deve conter apenas digitos (1 a 1000001 caracteres)")
    @Schema(example = "9875", description = "String representando um inteiro (1 <= n <= 10^1000000)")
    private String n;

    @Min(value = 1, message = "k deve ser >= 1")
    @Max(value = 100_000, message = "k deve ser <= 100000")
    @Schema(example = "4", description = "Numero de concatenacoes (1 <= k <= 10^5)")
    private int k;

    @Schema(example = "1", description = "Id do usuario para associar o calculo (opcional)", nullable = true)
    private Long usuarioId;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
