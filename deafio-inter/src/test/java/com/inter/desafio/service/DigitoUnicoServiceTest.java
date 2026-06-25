package com.inter.desafio.service;

import com.inter.desafio.config.DigitoUnicoCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fase 1 - Tarefa 1.1: cenarios basicos e extremos do calculo do digito unico.
 * Regra de Ouro: estes testes definem o comportamento; o codigo deve se adequar a eles.
 */
class DigitoUnicoServiceTest {

    private DigitoUnicoService service;

    @BeforeEach
    void setUp() {
        service = new DigitoUnicoService(new DigitoUnicoCache());
    }

    @Test
    @DisplayName("Numero de um unico digito retorna ele mesmo (k=1)")
    void digitoUnicoDeNumeroComUmDigito() {
        assertEquals(8, service.calcular("8", 1));
        assertEquals(0, service.calcular("0", 1));
        assertEquals(9, service.calcular("9", 1));
    }

    @Test
    @DisplayName("Exemplo da especificacao: digitoUnico(9875) = 2")
    void exemploEspecificacao9875() {
        assertEquals(2, service.calcular("9875", 1));
    }

    @Test
    @DisplayName("Exemplo da especificacao com concatenacao: n=9875, k=4 -> 8")
    void exemploEspecificacaoComConcatenacao() {
        assertEquals(8, service.calcular("9875", 4));
    }

    @Test
    @DisplayName("Exemplo do enunciado: digitoUnico(29) = 2, digitoUnico(116) = 8")
    void exemplosIntermediarios() {
        assertEquals(2, service.calcular("29", 1));
        assertEquals(8, service.calcular("116", 1));
    }

    @Test
    @DisplayName("Multiplos de 9 sempre resultam em 9 (raiz digital)")
    void multiplosDeNoveResultamEmNove() {
        assertEquals(9, service.calcular("9", 9));
        assertEquals(9, service.calcular("18", 1));
        assertEquals(9, service.calcular("99999", 1));
    }

    @Test
    @DisplayName("k aplica a concatenacao corretamente")
    void concatenacaoComK() {
        // n=1, k=10 -> P=1111111111 -> soma=10 -> 1+0=1
        assertEquals(1, service.calcular("1", 10));
        // n=5, k=2 -> P=55 -> 5+5=10 -> 1
        assertEquals(1, service.calcular("5", 2));
    }

    @Test
    @DisplayName("String gigante nao deve causar estouro de pilha nem overflow")
    void numeroGiganteNaoEstoura() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1_000_000; i++) {
            sb.append('9');
        }
        // soma dos digitos = 9_000_000, multiplo de 9 -> raiz digital 9
        long inicio = System.currentTimeMillis();
        int resultado = service.calcular(sb.toString(), 100_000);
        long duracao = System.currentTimeMillis() - inicio;
        assertEquals(9, resultado);
        assertTrue(duracao < 5_000, "Calculo deve ser rapido (foi " + duracao + "ms)");
    }

    @Test
    @DisplayName("Rejeita entradas invalidas")
    void rejeitaEntradasInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> service.calcular(null, 1));
        assertThrows(IllegalArgumentException.class, () -> service.calcular("", 1));
        assertThrows(IllegalArgumentException.class, () -> service.calcular("12a3", 1));
        assertThrows(IllegalArgumentException.class, () -> service.calcular("123", 0));
        assertThrows(IllegalArgumentException.class, () -> service.calcular("123", -5));
    }
}
