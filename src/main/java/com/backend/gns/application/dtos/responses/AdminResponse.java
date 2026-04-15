package com.backend.gns.application.dtos.responses;

import java.time.LocalDate;
import java.util.UUID;

import com.backend.gns.Shared.user.domain.enums.TypeRole;

import lombok.Builder;

@Builder
public record AdminResponse(

		UUID trackingId,

		String email,

		String nom,

		String prenom,

		TypeRole role,

		Boolean estActif,

		String telephone,

		LocalDate dateNaissance,

		String numeroCompte

) {
}

