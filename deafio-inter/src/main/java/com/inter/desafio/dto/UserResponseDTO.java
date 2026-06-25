package com.inter.desafio.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Representacao de saida de um usuario.
 *
 * O campo "criptografado" indica se nome/email retornados estao em texto cifrado
 * (quando criptografados com uma chave publica externa do cliente, o servidor nao
 * possui a chave privada para descriptografar) ou ja descriptografados.
 */
public class UserResponseDTO {

    private Long id;

    @Schema(description = "Nome do usuario (descriptografado quando possivel, senao o ciphertext em Base64)")
    private String nome;

    @Schema(description = "Email do usuario (descriptografado quando possivel, senao o ciphertext em Base64)")
    private String email;

    @Schema(description = "Indica se nome/email ainda estao criptografados (chave privada nao disponivel no servidor)")
    private boolean criptografado;

    private List<CalculoResponseDTO> calculos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isCriptografado() {
        return criptografado;
    }

    public void setCriptografado(boolean criptografado) {
        this.criptografado = criptografado;
    }

    public List<CalculoResponseDTO> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoResponseDTO> calculos) {
        this.calculos = calculos;
    }
}
