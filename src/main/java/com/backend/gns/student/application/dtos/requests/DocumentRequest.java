package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record DocumentRequest(
    UUID ownerTrackingId,
    TypeDocument documentType,
    String fileUrl,
    String providerPublicId,
    StatutDocument status,
    String rejectionComment,
    LocalDateTime uploadedAt) {}
