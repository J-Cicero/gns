package com.backend.gns.core.parametrage.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DocumentRequest(
        @NotNull UUID ownerTrackingId,
        @NotNull ProprietaireType ownerType,
        @NotNull TypeDocument documentType,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) String fileUrl,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY) String providerPublicId // Public ID from Cloudinary
) {}
