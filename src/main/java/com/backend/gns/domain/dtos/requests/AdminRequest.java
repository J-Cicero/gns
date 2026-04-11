package com.backend.gns.domain.dtos.requests;

public record AdminRequest(
        String nom,
        String prenom,
        String email,
        String motDePasse,
        String telephone,
        String grade
) {}
