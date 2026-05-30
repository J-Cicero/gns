package com.backend.gns.core.security.constants;

public class JavaConstant {

    public final static String FRONTEND_URL = "*";
    public final static String API_BASE_URL = "/api";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";

    public final static String[] PUBLIC_URLS = {
            API_BASE_URL + "/users/register",
            API_BASE_URL + "/users/login",
            API_BASE_URL + "/public/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/actuator/health",  // Health check pour Render
            "/actuator/info"
    };

    // URLs pour ADMINISTRATEUR central et backoffice métier.
    // Cela couvre la configuration du système, la validation KYC, les universités,
    // les wallets, les versements, la scolarité et les règles de bourse.
    public final static String[] ADMIN_URLS = {
            API_BASE_URL + "/admin/**",
            API_BASE_URL + "/admin-university/**",
            API_BASE_URL + "/admin-dbs/**",
            API_BASE_URL + "/kyc/**",
            API_BASE_URL + "/documents/validate/**",
            API_BASE_URL + "/documents/reject/**",
            API_BASE_URL + "/scolarite/**",
            API_BASE_URL + "/regles-bourse-dbs/**",
            API_BASE_URL + "/wallets/**",
            API_BASE_URL + "/versements/**",
            API_BASE_URL + "/universites/**",
            API_BASE_URL + "/parametres-gns/**"
    };

    // URLs pour ETUDIANT.
    // Le wallet est créé avec le compte, l'inscription annuelle déclenche le cycle.
    // Les commandes représentent l'achat côté application, pas un e-commerce classique.
    public final static String[] ETUDIANT_URLS = {
            API_BASE_URL + "/students/**",
            API_BASE_URL + "/wallets/**",
            API_BASE_URL + "/paiements/**",
            API_BASE_URL + "/commandes/**",
            API_BASE_URL + "/documents/**",
            API_BASE_URL + "/cards/**",
            API_BASE_URL + "/inscriptions/**",
            API_BASE_URL + "/scolarite/**"
    };

    // URLs pour COMMERCANT.
    // Le commerçant gère ses boutiques, produits et les commandes liées à son activité.
    public final static String[] COMMERCANT_URLS = {
            API_BASE_URL + "/merchants/**",
            API_BASE_URL + "/boutiques/**",
            API_BASE_URL + "/products/**",
            API_BASE_URL + "/commandes/**"
    };

    // URLs pour PORTAIL BANQUE.
    // La banque observe surtout les étudiants rattachés, les dépenses et les versements.
    public final static String[] BANQUE_URLS = {
            API_BASE_URL + "/bank-portal/**",
            API_BASE_URL + "/bank-operator/**",
            API_BASE_URL + "/versements/**",
            API_BASE_URL + "/wallets/**"
    };

    // URLs pour UNIVERSITY ADMIN.
    // Vue limitée aux paiements et aux informations nécessaires au suivi universitaire.
    public final static String[] UNIVERSITY_URLS = {
            API_BASE_URL + "/admin-university/**",
            API_BASE_URL + "/paiements/**",
            API_BASE_URL + "/stats/**"
    };

    // URLs pour ADMIN DBS.
    // Gestion des règles de bourse et des vues d'agrégation nécessaires au pilotage.
    public final static String[] DBS_URLS = {
            API_BASE_URL + "/admin-dbs/**",
            API_BASE_URL + "/stats/**",
            API_BASE_URL + "/regles-bourse-dbs/**"
    };

}
