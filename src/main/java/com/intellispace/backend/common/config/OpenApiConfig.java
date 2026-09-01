package com.intellispace.backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI intelliSpaceOpenApi() {
        String bearerScheme = "bearerAuth";
        return new OpenAPI()
                .info(new Info().title("IntelliSpace API").version("v1")
                        .description("Backend for the IntelliSpace 3D interior design platform."))
                .addSecurityItem(new SecurityRequirement().addList(bearerScheme))
                .components(new Components().addSecuritySchemes(bearerScheme,
                        new SecurityScheme().name(bearerScheme).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}