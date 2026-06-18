package com.backend.gns.core.application.dtos.requests;

import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.TargetType;

public record DocumentRequisRequest(
    String niveau,
    TargetType targetType, 
    TypeDocument typeDocument, 
    boolean obligatoire, 
    boolean estActif) {}
