package com.backend.gns.application.dtos.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.domain.enums.VersementType;

import lombok.Builder;

@Builder
public record VersementRequest(

        UUID trackingWalletId,

		BigDecimal montantVerse,

		VersementType typeVersement,

		LocalDateTime dateVersement,

		VersementStatut statut

) {
}

