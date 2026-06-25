package com.inter.desafio.config;

import com.inter.desafio.service.CryptoService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Gerencia as chaves RSA usadas pela aplicacao.
 *
 * Estrategia:
 *  - Na inicializacao, gera um par de chaves "do servidor". Esse par permite que a
 *    aplicacao funcione de ponta a ponta por padrao (criptografa e descriptografa).
 *  - O endpoint de chave publica permite ao cliente enviar a SUA propria chave publica;
 *    a partir dai, novos dados sao criptografados com a chave do cliente. Nesse cenario
 *    real, somente o cliente (dono da chave privada) consegue descriptografar.
 *
 * A descriptografia no servidor e sempre tentada com a chave privada do servidor; se a
 * tentativa falhar, significa que o dado foi cifrado com uma chave externa e permanece
 * cifrado na resposta (o cliente descriptografa localmente).
 */
@Component
public class KeyManager {

    private final CryptoService cryptoService;

    private KeyPair parDoServidor;
    private final AtomicReference<PublicKey> chavePublicaAtiva = new AtomicReference<>();

    public KeyManager(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostConstruct
    public void inicializar() {
        this.parDoServidor = cryptoService.gerarParDeChaves();
        this.chavePublicaAtiva.set(parDoServidor.getPublic());
    }

    /** Define a chave publica ativa (enviada pelo cliente) a partir de Base64. */
    public void definirChavePublica(String base64) {
        PublicKey nova = cryptoService.lerChavePublica(base64);
        this.chavePublicaAtiva.set(nova);
    }

    /** Chave publica usada atualmente para criptografar dados de novos usuarios. */
    public PublicKey getChavePublicaAtiva() {
        return chavePublicaAtiva.get();
    }

    public PrivateKey getChavePrivadaServidor() {
        return parDoServidor.getPrivate();
    }

    public PublicKey getChavePublicaServidor() {
        return parDoServidor.getPublic();
    }

    public String getChavePublicaServidorBase64() {
        return cryptoService.paraBase64(parDoServidor.getPublic());
    }

    public String getChavePublicaAtivaBase64() {
        return cryptoService.paraBase64(chavePublicaAtiva.get());
    }
}
