package com.inter.desafio.exception;

/**
 * Lancada quando um recurso (ex.: usuario) nao e encontrado.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }
}
