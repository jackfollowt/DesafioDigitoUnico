package com.inter.desafio.controller;

import com.inter.desafio.dto.CalculoResponseDTO;
import com.inter.desafio.dto.UserRequestDTO;
import com.inter.desafio.dto.UserResponseDTO;
import com.inter.desafio.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * CRUD completo de usuarios e recuperacao dos calculos de um usuario.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "CRUD de usuarios com nome/email criptografados (RSA 2048)")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Cria um novo usuario")
    public ResponseEntity<UserResponseDTO> criar(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO criado = userService.criar(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/" + criado.getId())).body(criado);
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuarios")
    public List<UserResponseDTO> listar() {
        return userService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um usuario por id")
    public UserResponseDTO buscar(@PathVariable Long id) {
        return userService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um usuario")
    public UserResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        return userService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um usuario")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        userService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/calculos")
    @Operation(summary = "Recupera todos os calculos de digito unico de um usuario")
    public List<CalculoResponseDTO> calculos(@PathVariable Long id) {
        return userService.listarCalculos(id);
    }
}
