package com.backend.gns.admin.application.dtos.responses;

import lombok.Builder;
import java.util.UUID;

@Builder
public record AdminULResponse(
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
