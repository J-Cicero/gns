package com.backend.gns.user.application.dtos.responses;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
    UUID trackingId,
    String nom,
    String prenom,
    String telephone,
    String email,
    String role,
    LocalDate dateInscription,
    boolean estActif) {}
