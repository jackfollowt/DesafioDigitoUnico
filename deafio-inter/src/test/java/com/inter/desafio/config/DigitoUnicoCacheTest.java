package com.inter.desafio.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fase 2 - Tarefa 2.1: cache mantem apenas os ultimos 10 calculos e e thread-safe.
 */
class DigitoUnicoCacheTest {

    @Test
    @DisplayName("12 requisicoes -> apenas as ultimas 10 ficam em memoria")
    void mantemApenasUltimosDez() {
        DigitoUnicoCache cache = new DigitoUnicoCache();

        for (int i = 1; i <= 12; i++) {
            cache.armazenar(String.valueOf(i), 1, i);
        }

        assertEquals(10, cache.tamanho());
        // As duas primeiras (1 e 2) devem ter sido descartadas.
        assertFalse(cache.contem("1", 1));
        assertFalse(cache.contem("2", 1));
        // As ultimas 10 (3..12) permanecem.
        for (int i = 3; i <= 12; i++) {
            assertTrue(cache.contem(String.valueOf(i), 1), "Esperado conter " + i);
        }
    }

    @Test
    @DisplayName("Calculo ja existente e recuperado do cache")
    void recuperaValorArmazenado() {
        DigitoUnicoCache cache = new DigitoUnicoCache();
        cache.armazenar("9875", 4, 8);

        assertTrue(cache.contem("9875", 4));
        assertEquals(8, cache.obter("9875", 4));
        assertNull(cache.obter("9875", 5));
    }

    @Test
    @DisplayName("Acesso (LRU) protege a entrada mais usada da remocao")
    void acessoProtegeEntrada() {
        DigitoUnicoCache cache = new DigitoUnicoCache();
        for (int i = 1; i <= 10; i++) {
            cache.armazenar(String.valueOf(i), 1, i);
        }
        // Acessa a entrada 1 -> torna-se a mais recente.
        cache.obter("1", 1);
        // Insere a 11 -> deve descartar a 2 (mais antiga), nao a 1.
        cache.armazenar("11", 1, 11);

        assertTrue(cache.contem("1", 1));
        assertFalse(cache.contem("2", 1));
        assertEquals(10, cache.tamanho());
    }

    @Test
    @DisplayName("Cache e thread-safe sob acesso concorrente")
    void threadSafe() throws InterruptedException {
        DigitoUnicoCache cache = new DigitoUnicoCache();
        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        AtomicInteger erros = new AtomicInteger();

        for (int t = 0; t < threads; t++) {
            final int base = t;
            pool.submit(() -> {
                try {
                    for (int i = 0; i < 1_000; i++) {
                        cache.armazenar((base + "_" + i), 1, i);
                        cache.contem((base + "_" + i), 1);
                        cache.tamanho();
                    }
                } catch (RuntimeException e) {
                    erros.incrementAndGet();
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));
        assertEquals(0, erros.get());
        assertEquals(DigitoUnicoCache.CAPACIDADE, cache.tamanho());
    }
}
