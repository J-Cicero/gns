package com.backend.gns.core.application.dtos.responses;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.TargetType;
import java.util.UUID;

public record DocumentRequisResponse(
    Long id,
    UUID trackingId,
    String niveau,
    TargetType targetType,
    TypeDocument typeDocument,
    boolean obligatoire,
    boolean estActif) {}
