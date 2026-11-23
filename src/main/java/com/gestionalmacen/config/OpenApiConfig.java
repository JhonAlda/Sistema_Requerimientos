package com.gestionalmacen.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gestionAlmacenOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("Sistema de Gestión de Requerimientos y Almacén")
            .description("API REST para requerimientos, despachos e inventario")
            .version("1.0.0")
            .contact(new Contact().name("Administración del sistema")));
    }
}
