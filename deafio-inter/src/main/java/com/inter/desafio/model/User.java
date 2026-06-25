package com.inter.desafio.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Usuario.
 *
 * Nome e email sao armazenados criptografados (RSA 2048) nos campos correspondentes.
 * Mantem a lista de resultados de digitos unicos ja calculados pelo usuario.
 */
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome do usuario armazenado criptografado (Base64 do ciphertext RSA). */
    @Column(name = "nome", nullable = false, length = 4096)
    private String nome;

    /** Email do usuario armazenado criptografado (Base64 do ciphertext RSA). */
    @Column(name = "email", nullable = false, length = 4096)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Calculo> calculos = new ArrayList<>();

    public User() {
    }

    public User(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public void adicionarCalculo(Calculo calculo) {
        calculo.setUser(this);
        this.calculos.add(calculo);
    }

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

    public List<Calculo> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<Calculo> calculos) {
        this.calculos = calculos;
    }
}
