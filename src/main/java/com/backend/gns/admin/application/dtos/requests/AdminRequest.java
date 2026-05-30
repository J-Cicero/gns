package com.backend.gns.admin.application.dtos.requests;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AdminRequest(
    String email,
    String password,
    String nom,
    String prenom,
    Boolean estActif,
    String telephone,
    LocalDateTime dateNaissance,
    String numeroCompte) {}
