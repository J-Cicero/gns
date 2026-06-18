package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;

import java.time.LocalDateTime;

public record DocumentEtudiantResponse(
    TypeDocument documentType,
    String fileUrl,
    StatutDocument status,
    LocalDateTime uploadedAt
) {}
