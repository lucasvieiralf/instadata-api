package br.com.grape.accessmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do SpringDoc/OpenAPI (Swagger).
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Access Manager API",
        version = "v1",
        description = "API para o sistema de gerenciamento de acesso e licenciamento.",
        license = @License(name = "Proprietário")
    )
)
public class OpenAPIConfig {

    /**
     * Configurar o "botão Authorize" global para aceitar o Bearer Token JWT.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(
                new Components()
                    .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP) // Tipo HTTP
                            .scheme("bearer")   // Esquema Bearer
                            .bearerFormat("JWT")    // Formato JWT
                            .in(SecurityScheme.In.HEADER)
                            .name("Authorization")  // Nome do Header
                    )
            );
    }
}