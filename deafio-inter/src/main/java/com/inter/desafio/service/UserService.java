package com.inter.desafio.service;

import com.inter.desafio.config.KeyManager;
import com.inter.desafio.dto.*;
import com.inter.desafio.exception.ResourceNotFoundException;
import com.inter.desafio.model.Calculo;
import com.inter.desafio.model.User;
import com.inter.desafio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Regras de negocio do CRUD de usuario, incluindo criptografia de nome/email e
 * associacao de calculos de digito unico.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final CryptoService cryptoService;
    private final KeyManager keyManager;

    public UserService(UserRepository repository, CryptoService cryptoService, KeyManager keyManager) {
        this.repository = repository;
        this.cryptoService = cryptoService;
        this.keyManager = keyManager;
    }

    @Transactional
    public UserResponseDTO criar(UserRequestDTO dto) {
        User user = new User();
        user.setNome(cryptoService.criptografar(dto.getNome(), keyManager.getChavePublicaAtiva()));
        user.setEmail(cryptoService.criptografar(dto.getEmail(), keyManager.getChavePublicaAtiva()));
        return paraResponse(repository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO buscarPorId(Long id) {
        return paraResponse(obterEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarTodos() {
        return repository.findAll().stream().map(this::paraResponse).collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO atualizar(Long id, UserRequestDTO dto) {
        User user = obterEntidade(id);
        user.setNome(cryptoService.criptografar(dto.getNome(), keyManager.getChavePublicaAtiva()));
        user.setEmail(cryptoService.criptografar(dto.getEmail(), keyManager.getChavePublicaAtiva()));
        return paraResponse(repository.save(user));
    }

    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario nao encontrado: " + id);
        }
        repository.deleteById(id);
    }

    /** Persiste um calculo associado a um usuario existente. */
    @Transactional
    public void registrarCalculo(Long usuarioId, String n, int k, int resultado) {
        User user = obterEntidade(usuarioId);
        user.adicionarCalculo(new Calculo(n, k, resultado));
        repository.save(user);
    }

    @Transactional(readOnly = true)
    public List<CalculoResponseDTO> listarCalculos(Long usuarioId) {
        User user = obterEntidade(usuarioId);
        return user.getCalculos().stream()
                .map(c -> new CalculoResponseDTO(c.getN(), c.getK(), c.getResultado()))
                .collect(Collectors.toList());
    }

    private User obterEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + id));
    }

    private UserResponseDTO paraResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());

        String nome = tentarDescriptografar(user.getNome());
        String email = tentarDescriptografar(user.getEmail());
        boolean cifrado = nome == null || email == null;

        dto.setCriptografado(cifrado);
        dto.setNome(cifrado ? user.getNome() : nome);
        dto.setEmail(cifrado ? user.getEmail() : email);

        dto.setCalculos(user.getCalculos().stream()
                .map(c -> new CalculoResponseDTO(c.getN(), c.getK(), c.getResultado()))
                .collect(Collectors.toList()));
        return dto;
    }

    /**
     * Tenta descriptografar com a chave privada do servidor. Retorna null se o dado
     * foi cifrado com uma chave externa (o servidor nao possui a privada correspondente).
     */
    private String tentarDescriptografar(String cipher) {
        try {
            return cryptoService.descriptografar(cipher, keyManager.getChavePrivadaServidor());
        } catch (RuntimeException e) {
            return null;
        }
    }
}
