package com.backend.gns.domain.dtos.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminResponse(
        UUID trackingId,
        String nom,
        String prenom,
        String email,
        String telephone,
        LocalDate dateInscription,
        boolean estActif,
        String grade,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
