package com.backend.gns.Shared.security.constants;

public class JavaConstant {

    public final static String FRONTEND_URL = "*";
    public final static String API_BASE_URL = "/api";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";

    public final static String[] PUBLIC_URLS = {
            "/users/**",
            API_BASE_URL + "/public/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/actuator/health",  // Health check pour Render
            "/actuator/info"
    };

    // URLs pour ADMINISTRATEUR (accès complet)
    public final static String[] ADMINISTRATEUR_URLS = {
            "/admin/**",
            "/users/all/**",
            "/config/**"
    };
    
    // URLs pour GESTIONNAIRE
    public final static String[] GESTIONNAIRE_URLS = {
            "/gestion/**",
            "/reports/**"
    };

    // URLs pour UTILISATEUR authentifié
    public final static String[] UTILISATEUR_URLS = {
            "/profile/**",
            "/dashboard/**"
    };
    
    // URLs pour CONSULTANT
    public final static String[] CONSULTANT_URLS = {
            "/consultations/**",
            "/analytics/**"
    };
    
    // URLs pour FREELANCE
    public final static String[] FREELANCE_URLS = {
            "/projects/**",
            "/tasks/**"
    };

}
