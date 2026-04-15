package com.backend.gns.application.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.domain.enums.CommandeStatut;

import lombok.Builder;

@Builder

public record CommandeRequest(

	    String reference,

		UUID studentTrackingId,

		UUID merchantTrackingId,

		BigDecimal montantTotal,

		LocalDateTime dateCommande,

		CommandeStatut statut

) {
}

