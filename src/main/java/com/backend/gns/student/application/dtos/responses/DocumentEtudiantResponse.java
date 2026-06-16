package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StatutDocument;
import java.time.LocalDateTime;

public record DocumentEtudiantResponse(
    TypeDocument documentType,
    String fileUrl,
    StatutDocument status,
    LocalDateTime uploadedAt
) {}
