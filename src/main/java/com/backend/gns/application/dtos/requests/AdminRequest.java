package com.backend.gns.application.dtos.requests;

import java.time.LocalDate;

import com.backend.gns.Shared.user.domain.enums.TypeRole;

import lombok.Builder;

@Builder
public record AdminRequest(

		String email,

		String password,

		String nom,

		String prenom,

		TypeRole role,

		Boolean estActif,

		String telephone,

		LocalDate dateNaissance,

		String numeroCompte

) {
}

