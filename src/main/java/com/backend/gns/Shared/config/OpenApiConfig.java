package com.backend.gns.Shared.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;


@Configuration
public class OpenApiConfig {
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Titan Hisaa Backend API")
                                .description(
                                        "API de la plateforme de gestion d'évènements")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("NitchCorp Team")
                                                .email("nitchcorp@gmail.com")
                                                .url("https://nitchcorp.com"))
                                .license(new License().name("Proprietary").url("https://nitchcorp.com/license")))
                .servers(
                        List.of(
                                new Server().url(contextPath).description("Serveur de développement"),
                                new Server()
                                        .url("https://titan-hisaa-backend-springboot.onrender.com/api")
                                        .description("Serveur de production")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        "Entrez votre token JWT. Format: Bearer {token}\n\n"
                                                                + "Pour obtenir un token:\n"
                                                                + "1. Utilisez l'endpoint POST /user/login avec vos identifiants\n"
                                                                + "2. Copiez le token reçu dans la réponse\n"
                                                                + "3. Cliquez sur le bouton 'Authorize' (cadenas) ci-dessus\n"
                                                                + "4. Collez le token dans le champ 'Value'\n"
                                                                + "5. Cliquez sur 'Authorize' puis 'Close'\n\n"
                                                                + "Le token sera automatiquement ajouté à toutes les requêtes.")));
    }
}
