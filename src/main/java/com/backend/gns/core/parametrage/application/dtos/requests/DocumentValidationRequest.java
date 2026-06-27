package com.backend.gns.core.parametrage.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.StatutDocument;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentValidationRequest {

    @NotNull(message = "Le statut est obligatoire")
    private StatutDocument status;

    private String rejectionReason;
}
