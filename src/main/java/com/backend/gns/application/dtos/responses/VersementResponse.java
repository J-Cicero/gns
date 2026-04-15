package com.backend.gns.application.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.backend.gns.domain.enums.VersementStatut;
import com.backend.gns.domain.enums.VersementType;

import lombok.Builder;

@Builder
public record VersementResponse(

		UUID trackingId,

		UUID trackingWalletId,

		BigDecimal montantVerse,

		VersementType typeVersement,

		LocalDateTime dateVersement,

		VersementStatut statut

) {
}
