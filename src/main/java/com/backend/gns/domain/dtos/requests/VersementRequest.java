package com.backend.gns.domain.dtos.requests;

import java.time.LocalDate;
import java.util.UUID;

public record VersementRequest(
        UUID walletTrackingId,
        Double montantVerse,
        String typeVersement,
        LocalDate datePrevue
) {}
