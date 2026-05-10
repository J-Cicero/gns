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
                                .title("MiabéShop API - GNS Backend")
                                .description(
                                        "Complete API documentation for MiabéShop - Student Payment & Commerce Platform\n\n" +
                                        "## Features\n" +
                                        "- **Student Management**: Registration, KYC validation, wallet management\n" +
                                        "- **Merchant Management**: Shop registration, product catalog, budget tracking\n" +
                                        "- **Payment System**: Multi-wallet support (Relais, Horizon), commission calculation\n" +
                                        "- **Orders**: Complete order lifecycle from creation to payment\n" +
                                        "- **Virtual Budget**: Monthly merchant budget allocation and tracking\n\n" +
                                        "## Authentication\n" +
                                        "All endpoints (except /auth/login and /auth/register) require JWT Bearer token")
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("MiabéShop Team")
                                                .email("support@miabeshop.cm")
                                                .url("https://miabeshop.cm"))
                                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(
                        List.of(
                                new Server().url("http://localhost:8080").description("Serveur de développement"),
                                new Server()
                                        .url("https://gns-api.production.com")
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
                                                        "JWT Bearer Token\n\n" +
                                                        "Pour obtenir un token:\n" +
                                                        "1. Utilisez l'endpoint POST /api/user/login avec vos identifiants\n" +
                                                        "2. Copiez le token reçu\n" +
                                                        "3. Cliquez sur le bouton 'Authorize' (cadenas) ci-dessus\n" +
                                                        "4. Entrez: Bearer {token}\n" +
                                                        "5. Le token sera automatiquement ajouté à toutes les requêtes")));
    }
}
