package com.backend.gns.domain.services;

import com.backend.gns.application.dtos.requests.AuthRequest;
import com.backend.gns.application.dtos.responses.AuthResponse;

/**
 * Service pour gérer l'authentification des utilisateurs.
 */
public interface AuthService {

    /**
     * Authentifie un utilisateur via email et mot de passe.
     *
     * @param request requête d'authentification
     * @return réponse avec le token JWT
     */
    AuthResponse login(AuthRequest request);
}
