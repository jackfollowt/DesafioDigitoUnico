package com.inter.desafio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao da documentacao OpenAPI/Swagger.
 * UI disponivel em /swagger-ui.html e especificacao em /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI desafioOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Desafio Digito Unico - Banco Inter")
                .description("API REST para calculo de Digito Unico e CRUD de Usuario com criptografia RSA 2048 bits.")
                .version("1.0")
                .contact(new Contact().name("Time Alice").email("alice.castilho@inter.co"))
                .license(new License().name("Desafio Tecnico")));
    }
}
