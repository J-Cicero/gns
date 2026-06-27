package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;

import java.time.LocalDateTime;
import java.util.UUID;


public record DocumentEtudiantResponse(
        UUID trackingId,
        TypeDocument documentType,
        String fileUrl,
        StatutDocument status,
        LocalDateTime uploadedAt,
        String rejectionReason,
        UUID inscriptionTrackingId,
        String ownerType
) {}