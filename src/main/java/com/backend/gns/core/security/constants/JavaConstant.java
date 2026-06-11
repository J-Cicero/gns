package com.backend.gns.core.security.constants;

public class JavaConstant {

  public static final String FRONTEND_URL = "*";
  public static final String API_BASE_URL = "";
  public static final String OPTIONS_HTTP_METHOD = "OPTIONS";

  public static final String[] PUBLIC_URLS = {
    API_BASE_URL + "/users/login",
    API_BASE_URL + "/public/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/v3/api-docs/**",
    "/api-docs/**",
    "/actuator/health", // Health check pour Render
    "/actuator/info"
  };

  public static final String[] CAMPAGNE_GNS_URLS = {API_BASE_URL + "/campagnes/*/process"};

  // URLs exclusives pour ADMINISTRATEUR central (ADMIN_GNS)
  public static final String[] ADMIN_URLS = {
    API_BASE_URL + "/admins/**",
    API_BASE_URL + "/users/register",
    API_BASE_URL + "/kyc/**",
    API_BASE_URL + "/documents/validate/**",
    API_BASE_URL + "/documents/reject/**",
    API_BASE_URL + "/parametres-gns/**"
  };

  // URLs exclusives pour ETUDIANT
  public static final String[] ETUDIANT_URLS = {
    API_BASE_URL + "/students/**",
    API_BASE_URL + "/documents/**",
    API_BASE_URL + "/cards/**",
    API_BASE_URL + "/inscriptions/**",
    API_BASE_URL + "/scolarite/**",
    API_BASE_URL + "/scolarite-years/**",
    API_BASE_URL + "/documents-requis/**"
  };

  // URLs exclusives pour COMMERCANT
  public static final String[] COMMERCANT_URLS = {
    API_BASE_URL + "/merchants/**", API_BASE_URL + "/boutiques/**", API_BASE_URL + "/products/**"
  };

  // URLs exclusives pour PORTAIL BANQUE (ADMIN_BANQUE)
  public static final String[] BANQUE_URLS = {
    API_BASE_URL + "/bank-portal/**", API_BASE_URL + "/bank-operator/**"
  };

  // URLs PARTAGÉES (avec gestion de rôles multiples)
  public static final String[] WALLETS_URLS = {API_BASE_URL + "/wallets/**"};

  public static final String[] VERSEMENTS_URLS = {API_BASE_URL + "/versements/**"};

  public static final String[] PAIEMENTS_URLS = {API_BASE_URL + "/paiements/**"};

  public static final String[] COMMANDES_URLS = {API_BASE_URL + "/commandes/**"};

  public static final String[] STATS_URLS = {API_BASE_URL + "/stats/**"};

  public static final String[] UNIVERSITES_URLS = {API_BASE_URL + "/universites/**"};
}
