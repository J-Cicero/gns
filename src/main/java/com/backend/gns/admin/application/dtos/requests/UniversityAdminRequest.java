package com.backend.gns.admin.application.dtos.requests;

import lombok.Builder;
import java.util.UUID;

@Builder
public record UniversityAdminRequest(
    String email,
    String password,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    String numeroCompte,
    UUID walletTrackingId,
    UUID universiteTrackingId
) {}
