package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DocumentResponse(
    UUID trackingId,
    UUID ownerTrackingId,
    TypeDocument documentType,
    String fileUrl,
    String providerPublicId,
    StatutDocument status,
    String rejectionComment,
    LocalDateTime uploadedAt) {}
