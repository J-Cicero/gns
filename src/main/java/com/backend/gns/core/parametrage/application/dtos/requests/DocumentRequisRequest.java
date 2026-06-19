package com.backend.gns.core.parametrage.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StudentNiveau;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DocumentRequisRequest(
        @NotNull TypeDocument typeDocument,
        @NotNull StudentNiveau studentNiveau,
        @NotNull Boolean required,
        String description
) {}
