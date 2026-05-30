package com.backend.gns.student.application.dtos.responses;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StudentNiveau;

public record DocumentRequisResponse(
    Long id,
    StudentNiveau niveau,
    TypeDocument typeDocument,
    boolean obligatoire,
    boolean estActif
) {}
