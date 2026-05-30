package com.backend.gns.student.application.dtos.requests;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.domain.enums.StudentNiveau;

public record DocumentRequisRequest(
    StudentNiveau niveau,
    TypeDocument typeDocument,
    boolean obligatoire,
    boolean estActif
) {}
