package com.backend.gns.core.parametrage.application.dtos.responses;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DocumentRequisResponse(
    UUID trackingId,
    TypeDocument typeDocument,
    Boolean required,
    String description
) {}
