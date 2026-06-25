package com.inter.desafio.service;

import com.inter.desafio.config.DigitoUnicoCache;
import org.springframework.stereotype.Service;

/**
 * Fase 1 - Core da regra de negocio: calculo do Digito Unico.
 *
 * Definicao:
 *   - Se x tem apenas um digito, o digito unico e x.
 *   - Caso contrario, o digito unico de x e o digito unico da soma dos seus digitos.
 *
 * Parametros: dados n (string de um inteiro) e k (numero de concatenacoes), forma-se
 * P = n repetido k vezes e calcula-se o digito unico de P.
 *
 * Otimizacao matematica (evita estouro de pilha/memoria com numeros gigantes):
 *   - O digito unico equivale a "raiz digital" (digital root).
 *   - soma_digitos(P) = k * soma_digitos(n).
 *   - raiz_digital(v) = 0 se v == 0; senao 1 + (v - 1) % 9.
 *
 * Assim nao e necessario materializar P (que poderia ter ate ~10^5 * 10^6 digitos);
 * basta uma unica varredura linear sobre n.
 */
@Service
public class DigitoUnicoService {

    private final DigitoUnicoCache cache;

    public DigitoUnicoService(DigitoUnicoCache cache) {
        this.cache = cache;
    }

    /**
     * Calcula o digito unico de P = n concatenado k vezes.
     *
     * @param n string representando um inteiro (1 <= n <= 10^1000000)
     * @param k numero de concatenacoes (1 <= k <= 10^5)
     * @return o digito unico (0..9)
     */
    public int calcular(String n, int k) {
        validar(n, k);

        Integer emCache = cache.obter(n, k);
        if (emCache != null) {
            return emCache;
        }

        long somaDigitosN = somaDosDigitos(n);
        long somaTotal = somaDigitosN * (long) k;
        int resultado = raizDigital(somaTotal);

        cache.armazenar(n, k, resultado);
        return resultado;
    }

    private void validar(String n, int k) {
        if (n == null || n.isEmpty()) {
            throw new IllegalArgumentException("n nao pode ser nulo ou vazio");
        }
        for (int i = 0; i < n.length(); i++) {
            char c = n.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("n deve conter apenas digitos (0-9)");
            }
        }
        if (k < 1) {
            throw new IllegalArgumentException("k deve ser maior ou igual a 1");
        }
    }

    private long somaDosDigitos(String n) {
        long soma = 0;
        for (int i = 0; i < n.length(); i++) {
            soma += n.charAt(i) - '0';
        }
        return soma;
    }

    private int raizDigital(long valor) {
        if (valor == 0) {
            return 0;
        }
        return (int) (1 + (valor - 1) % 9);
    }
}
