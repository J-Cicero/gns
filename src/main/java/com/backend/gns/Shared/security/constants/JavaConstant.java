package com.backend.gns.Shared.security.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JavaConstant {

    public static final String[] PUBLIC_URLS = {
            "/users/**",
            "/api/auth/login",
            "/api/students",
            "/api/merchants",
            "/api/public/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/actuator/health",
            "/actuator/info"
    };

    @Value("${app.frontend.url:*}")
    public String FRONTEND_URL;

    @Value("${app.api.base-url:/api}")
    public String API_BASE_URL;

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    public String CORS_ALLOWED_ORIGINS;

    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final String[] ADMIN_URLS = {
            "/api/students/*/valider-kyc",
            "/api/wallets/*/crediter-horizon",
            "/api/wallets/*/deverrouiller",
            "/api/budgets/allouer",
            "/api/versements/*/executer-remboursement",
            "/api/admins/**"
    };

    public static final String[] STUDENT_URLS = {
            "/api/wallets/*/recharger",
            "/api/paiements/effectuer",
            "/api/paiements/effectuer-hybride",
            "/api/paiements/scolarite",
            "/api/students/*",
            "/api/wallets/*",
            "/api/commandes/*"
    };

    public static final String[] MERCHANT_URLS = {
            "/api/products/**",
            "/api/budgets/*",
            "/api/merchants/*"
    };

    public static final String[] DBS_URLS = {
            "/api/versements/**",
            "/api/students/**",
            "/api/paiements/**"
    };

    public static final String[] ADMIN_DBS_URLS = {
            "/api/paiements/**"
    };
}
