package com.inter.desafio.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Valida o fluxo RSA 2048: criptografar com a chave publica e descriptografar com a privada.
 */
class CryptoServiceTest {

    private final CryptoService crypto = new CryptoService();

    @Test
    @DisplayName("Round-trip: cifrar com chave publica e decifrar com a privada")
    void roundTrip() {
        KeyPair par = crypto.gerarParDeChaves();
        String original = "Maria Silva <maria.silva@email.com>";

        String cifrado = crypto.criptografar(original, par.getPublic());
        assertNotEquals(original, cifrado);

        String decifrado = crypto.descriptografar(cifrado, par.getPrivate());
        assertEquals(original, decifrado);
    }

    @Test
    @DisplayName("Gera chave RSA de 2048 bits")
    void tamanhoDaChave() {
        KeyPair par = crypto.gerarParDeChaves();
        java.security.interfaces.RSAPublicKey pub =
                (java.security.interfaces.RSAPublicKey) par.getPublic();
        assertEquals(2048, pub.getModulus().bitLength());
    }

    @Test
    @DisplayName("Funciona via chaves em Base64 (X.509/PKCS#8)")
    void viaBase64() {
        KeyPair par = crypto.gerarParDeChaves();
        String pubB64 = crypto.paraBase64(par.getPublic());

        String cifrado = crypto.criptografar("dado sensivel", pubB64);
        String decifrado = crypto.descriptografar(cifrado, par.getPrivate());

        assertEquals("dado sensivel", decifrado);
        // garante que conseguimos reconstruir a chave publica a partir do Base64
        assertNotNull(crypto.lerChavePublica(pubB64));
    }

    @Test
    @DisplayName("Chave publica invalida lanca IllegalArgumentException")
    void chaveInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> crypto.lerChavePublica("nao-e-uma-chave"));
    }

    @Test
    @DisplayName("Cada usuario pode ter par de chaves distinto")
    void chavesDistintasPorUsuario() {
        KeyPair usuarioA = crypto.gerarParDeChaves();
        KeyPair usuarioB = crypto.gerarParDeChaves();

        String cifradoA = crypto.criptografar("segredo-A", usuarioA.getPublic());

        // A chave privada de B nao deve conseguir decifrar o que foi cifrado para A.
        assertThrows(IllegalStateException.class,
                () -> crypto.descriptografar(cifradoA, usuarioB.getPrivate()));
        // A chave privada de A decifra corretamente.
        assertEquals("segredo-A", crypto.descriptografar(cifradoA, usuarioA.getPrivate()));
    }
}
