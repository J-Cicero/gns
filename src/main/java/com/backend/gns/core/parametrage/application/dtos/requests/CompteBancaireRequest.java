package com.backend.gns.core.parametrage.application.dtos.requests;


import java.util.UUID;
public record CompteBancaireRequest(
        String accountNumber,
        UUID bankTrackingId
) {}