package com.backend.gns.application.dtos.responses;

import com.backend.gns.Shared.user.domain.enums.TypeRole;

import java.util.UUID;

/**
 * DTO pour les réponses d'authentification avec le token JWT.
 */
public record AuthResponse(
        UUID trackingId,
        String email,
        String nom,
        String prenom,
        TypeRole role,
        String token
) {}
