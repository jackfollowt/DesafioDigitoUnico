package com.inter.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Resultado de um calculo de digito unico.
 */
public class CalculoResponseDTO {

    @Schema(example = "9875")
    private String n;

    @Schema(example = "4")
    private int k;

    @Schema(example = "8", description = "Digito unico calculado")
    private int resultado;

    @Schema(description = "Indica se o resultado veio do cache em memoria")
    private boolean origemCache;

    public CalculoResponseDTO() {
    }

    public CalculoResponseDTO(String n, int k, int resultado) {
        this.n = n;
        this.k = k;
        this.resultado = resultado;
    }

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

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    public boolean isOrigemCache() {
        return origemCache;
    }

    public void setOrigemCache(boolean origemCache) {
        this.origemCache = origemCache;
    }
}
