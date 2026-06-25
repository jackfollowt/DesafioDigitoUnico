package com.inter.desafio.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Servico de criptografia assimetrica RSA 2048 bits.
 *
 * Fluxo:
 *  - Criptografia com a chave publica (do cliente).
 *  - Descriptografia com a chave privada (do cliente).
 *
 * Os metodos trabalham com chaves em Base64 (formato X.509/SPKI para publica e
 * PKCS#8 para privada) para facilitar o transporte via API.
 */
@Service
public class CryptoService {

    private static final String ALGORITMO = "RSA";
    private static final String TRANSFORMACAO = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int TAMANHO_CHAVE = 2048;

    /** Gera um novo par de chaves RSA 2048 bits. */
    public KeyPair gerarParDeChaves() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITMO);
            generator.initialize(TAMANHO_CHAVE);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo RSA indisponivel", e);
        }
    }

    /** Criptografa um texto usando a chave publica informada; retorna o ciphertext em Base64. */
    public String criptografar(String textoPlano, PublicKey chavePublica) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
            cipher.init(Cipher.ENCRYPT_MODE, chavePublica);
            byte[] cifrado = cipher.doFinal(textoPlano.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cifrado);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao criptografar", e);
        }
    }

    /** Criptografa usando uma chave publica em Base64 (X.509/SPKI). */
    public String criptografar(String textoPlano, String chavePublicaBase64) {
        return criptografar(textoPlano, lerChavePublica(chavePublicaBase64));
    }

    /** Descriptografa um ciphertext em Base64 usando a chave privada informada. */
    public String descriptografar(String cipherBase64, PrivateKey chavePrivada) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
            cipher.init(Cipher.DECRYPT_MODE, chavePrivada);
            byte[] decifrado = cipher.doFinal(Base64.getDecoder().decode(cipherBase64));
            return new String(decifrado, java.nio.charset.StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao descriptografar", e);
        }
    }

    /** Le uma chave publica RSA a partir de Base64 (X.509/SPKI), tolerando cabecalhos PEM. */
    public PublicKey lerChavePublica(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(limparPem(base64));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            return KeyFactory.getInstance(ALGORITMO).generatePublic(spec);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Chave publica invalida", e);
        }
    }

    /** Le uma chave privada RSA a partir de Base64 (PKCS#8), tolerando cabecalhos PEM. */
    public PrivateKey lerChavePrivada(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(limparPem(base64));
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
            return KeyFactory.getInstance(ALGORITMO).generatePrivate(spec);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Chave privada invalida", e);
        }
    }

    /** Codifica uma chave em Base64. */
    public String paraBase64(Key chave) {
        return Base64.getEncoder().encodeToString(chave.getEncoded());
    }

    private String limparPem(String valor) {
        return valor
                .replaceAll("-----BEGIN (?:RSA )?(?:PUBLIC|PRIVATE) KEY-----", "")
                .replaceAll("-----END (?:RSA )?(?:PUBLIC|PRIVATE) KEY-----", "")
                .replaceAll("\\s", "");
    }
}
