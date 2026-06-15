package com.backend.gns.student.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record UniversiteRequest(
    @NotBlank String code, 
    @NotBlank String fullName, 
    @NotBlank String city, 
    boolean isActive) {}
