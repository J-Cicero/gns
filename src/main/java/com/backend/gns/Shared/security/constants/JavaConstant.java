package com.backend.gns.Shared.security.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JavaConstant {

    // URLs statiques publiques (ne changent pas entre environnements)
    public static final String[] PUBLIC_URLS = {
            "/users/**",
            "/api/public/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/actuator/health",
            "/actuator/info"
    };

    // Injectées depuis application-{profile}.properties
    @Value("${app.frontend.url:*}")
    public String FRONTEND_URL;

    @Value("${app.api.base-url:/api}")
    public String API_BASE_URL;

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    public String CORS_ALLOWED_ORIGINS;

    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    // URLs par rôle - statiques mais peuvent être ajustées au besoin
    public static final String[] ADMINISTRATEUR_URLS = {
            "/api/admin/**"
    };

    public static final String[] GESTIONNAIRE_URLS = {
            "/api/manager/**"
    };

    public static final String[] UTILISATEUR_URLS = {
            "/api/user/**"
    };

    public static final String[] CONSULTANT_URLS = {
            "/api/consultant/**"
    };

    public static final String[] FREELANCE_URLS = {
            "/api/freelance/**"
    };
}
