package com.backend.gns.admin.application.dtos.requests;

import com.backend.gns.Shared.user.domain.enums.UserRole;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record AdminRequest(
    String email,
    String password,
    String nom,
    String prenom,
    UserRole role,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance,
    String numeroCompte) {}
