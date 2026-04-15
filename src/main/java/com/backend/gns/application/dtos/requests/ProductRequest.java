package com.backend.gns.application.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ProductRequest(

 UUID merchantTrackingId,

		String nom,

		String description,

		BigDecimal prix,

		int stock,

		Boolean estDisponible,

        LocalDateTime dateAjout

) {
}

