package com.backend.gns.student.application.dtos.responses;

import lombok.Builder;
import java.util.UUID;

@Builder
public record UniversityAdminResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    String numeroCompte,
    UUID walletTrackingId,
    UUID universiteTrackingId
) {}
