package com.backend.gns.user.application.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @Email(message = "Veillez metttre un email valide")
        @NotNull(message = "L'email est obligatoire")
        String email,
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caracteres")
        @NotNull(message = "Le mot de passe est obligatoire")
        String password) {}
