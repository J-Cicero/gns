package com.backend.gns.application.dtos.responses;

import com.backend.gns.Shared.user.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record AdminResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    UserRole role,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance,
    String numeroCompte) {}
