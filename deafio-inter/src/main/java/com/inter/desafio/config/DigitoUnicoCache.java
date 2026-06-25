package com.inter.desafio.config;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fase 2 - Cache personalizado (sem frameworks de mercado).
 *
 * Mantem em memoria os ultimos 10 calculos de digito unico, de forma independente
 * do usuario. Implementado manualmente como um LRU thread-safe sobre LinkedHashMap
 * em modo de ordenacao por acesso, removendo a entrada mais antiga quando excede a
 * capacidade.
 *
 * Se um calculo (mesma chave n|k) ja existe no cache, o valor armazenado e retornado
 * sem reexecutar a funcao.
 */
@Component
public class DigitoUnicoCache {

    /** Capacidade exigida pela especificacao: ultimos 10 calculos. */
    public static final int CAPACIDADE = 10;

    private final Map<String, Integer> cache;

    public DigitoUnicoCache() {
        // accessOrder = true -> LRU. synchronizedMap garante thread-safety basica;
        // os metodos publicos sincronizam o bloco composto (get/contains/put).
        LinkedHashMap<String, Integer> lru = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > CAPACIDADE;
            }
        };
        this.cache = Collections.synchronizedMap(lru);
    }

    private String chave(String n, int k) {
        return n + "|" + k;
    }

    public boolean contem(String n, int k) {
        synchronized (cache) {
            return cache.containsKey(chave(n, k));
        }
    }

    public Integer obter(String n, int k) {
        synchronized (cache) {
            return cache.get(chave(n, k));
        }
    }

    public void armazenar(String n, int k, int resultado) {
        synchronized (cache) {
            cache.put(chave(n, k), resultado);
        }
    }

    public int tamanho() {
        synchronized (cache) {
            return cache.size();
        }
    }

    public void limpar() {
        synchronized (cache) {
            cache.clear();
        }
    }
}
