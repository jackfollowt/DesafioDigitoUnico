package com.inter.desafio.model;

import jakarta.persistence.*;

/**
 * Entidade que representa um calculo de digito unico ja realizado.
 * Guarda os parametros de entrada (n, k) e o resultado obtido.
 */
@Entity
@Table(name = "calculos")
public class Calculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Parametro de entrada n (string do inteiro). */
    @Column(name = "entrada_n", nullable = false, length = 4096)
    private String n;

    /** Parametro de entrada k (numero de concatenacoes). */
    @Column(name = "entrada_k", nullable = false)
    private int k;

    /** Resultado do calculo do digito unico. */
    @Column(name = "resultado", nullable = false)
    private int resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private User user;

    public Calculo() {
    }

    public Calculo(String n, int k, int resultado) {
        this.n = n;
        this.k = k;
        this.resultado = resultado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
