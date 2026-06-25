package com.inter.desafio.controller;

import com.inter.desafio.config.DigitoUnicoCache;
import com.inter.desafio.dto.CalculoRequestDTO;
import com.inter.desafio.dto.CalculoResponseDTO;
import com.inter.desafio.service.DigitoUnicoService;
import com.inter.desafio.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint para calculo do digito unico, com associacao opcional a um usuario.
 */
@RestController
@RequestMapping("/api/digito-unico")
@Tag(name = "Digito Unico", description = "Calculo do digito unico de P = n concatenado k vezes")
public class CalculoController {

    private final DigitoUnicoService digitoUnicoService;
    private final DigitoUnicoCache cache;
    private final UserService userService;

    public CalculoController(DigitoUnicoService digitoUnicoService,
                             DigitoUnicoCache cache,
                             UserService userService) {
        this.digitoUnicoService = digitoUnicoService;
        this.cache = cache;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Calcula o digito unico; associa ao usuario se usuarioId for informado")
    public CalculoResponseDTO calcular(@Valid @RequestBody CalculoRequestDTO dto) {
        boolean origemCache = cache.contem(dto.getN(), dto.getK());

        int resultado = digitoUnicoService.calcular(dto.getN(), dto.getK());

        CalculoResponseDTO resposta = new CalculoResponseDTO(dto.getN(), dto.getK(), resultado);
        resposta.setOrigemCache(origemCache);

        if (dto.getUsuarioId() != null) {
            userService.registrarCalculo(dto.getUsuarioId(), dto.getN(), dto.getK(), resultado);
        }
        return resposta;
    }
}
