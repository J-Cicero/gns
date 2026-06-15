package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StatutDocument;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

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
