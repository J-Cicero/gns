package com.backend.gns.commerce.application.dtos.requests;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record MerchantRequest(
    String email,
    String password,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance
) {}
