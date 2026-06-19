package com.backend.gns.core.parametrage.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StudentNiveau;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DocumentRequisResponse(
    UUID trackingId,
    TypeDocument typeDocument,
    @NotNull StudentNiveau studentNiveau,
    Boolean required,
    String description
) {}
