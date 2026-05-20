package com.backend.gns.admin.application.dtos.requests;

import java.util.UUID;
import lombok.Builder;

@Builder
public record BankOperatorRequest(
    String email,
    String password,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    UUID walletTrackingId
) {}
