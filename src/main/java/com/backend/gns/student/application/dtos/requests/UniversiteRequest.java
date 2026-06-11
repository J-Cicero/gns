package com.backend.gns.student.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record UniversiteRequest(
    @NotBlank String code, 
    @NotBlank String nom, 
    @NotBlank String ville, 
    @NotBlank boolean estActive) {}
