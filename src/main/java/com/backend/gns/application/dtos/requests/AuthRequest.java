package com.backend.gns.application.dtos.requests;

/**
 * DTO pour les requêtes d'authentification.
 */
public record AuthRequest(
        String email,
        String password
) {}
