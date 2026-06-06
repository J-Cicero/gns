package com.backend.gns.user.application.dtos.responses;

import java.util.UUID;
import lombok.Builder;

@Builder
public record BankOperatorResponse(
    UUID trackingId,
    String email,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    UUID walletTrackingId,
    UUID banquePartenaireTrackingId) {}
