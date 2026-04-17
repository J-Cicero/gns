package com.backend.gns.Shared.user.application.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @Size(min = 2, message = "Le nom doit contenir au moins deux caractères")
        @NotNull(message = "Le nom est obligatoire")
        String nom,
    @Size(min = 2, message = "Le prénom doit contenir au moins deux caractères")
        @NotNull(message = "Le prénom est obligatoire")
        String prenom,
    @Pattern(
            regexp = "^(\\+?[0-9]{8,15})$",
            message = "Le numéro de téléphone doit être valide (8 à 15 chiffres, optionnel +)")
        @NotNull(message = "Le telephone est obligatoire")
        String telephone,
    @Email(message = "Veuillez mettre un email valide")
        @NotNull(message = "L'email est obligatoire")
        String email,
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caracteres")
        @NotNull(message = "Le mot de passe est obligatoire")
        String motDePasse) {}
