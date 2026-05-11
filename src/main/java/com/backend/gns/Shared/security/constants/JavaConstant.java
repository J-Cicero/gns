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

    // URLs pour ADMINISTRATEUR (ADMIN_GNS, ADMIN_UL, ADMIN_BANQUE, ADMIN_DBS)
    public final static String[] ADMIN_URLS = {
            "/admin/**",
            "/api/admin/**",
            "/api/admin-ul/**",
            "/api/admin-dbs/**",
            "/api/kyc/**",
            "/api/documents/validate/**",
            "/api/documents/reject/**"
    };

    // URLs pour ETUDIANT
    public final static String[] ETUDIANT_URLS = {
            "/api/students/**",
            "/api/wallets/**",
            "/api/paiements/**",
            "/api/commandes/**",
            "/api/documents/**",
            "/api/cards/**",
            "/api/inscriptions/**"
    };

    // URLs pour COMMERCANT
    public final static String[] COMMERCANT_URLS = {
            "/api/merchants/**",
            "/api/boutiques/**",
            "/api/products/**"
    };

    // URLs pour PORTAIL BANQUE
    public final static String[] BANQUE_URLS = {
            "/api/bank-portal/**",
            "/api/bank-operator/**",
            "/api/versements/**"
    };

    // URLs pour ADMIN UL
    public final static String[] UL_URLS = {
            "/api/admin-ul/**"
    };

    // URLs pour ADMIN DBS
    public final static String[] DBS_URLS = {
            "/api/admin-dbs/**",
            "/api/stats/**"
    };

}
