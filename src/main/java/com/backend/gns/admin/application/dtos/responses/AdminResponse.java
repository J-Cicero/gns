package com.backend.gns.admin.application.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record AdminResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance,
    String numeroCompte) {}
