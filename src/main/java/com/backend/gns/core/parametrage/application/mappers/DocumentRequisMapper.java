package com.backend.gns.core.parametrage.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.core.parametrage.domain.models.DocumentRequis;
import org.springframework.stereotype.Component;

@Component
public class DocumentRequisMapper {

    public DocumentRequis toEntity(DocumentRequisRequest request) {
        return DocumentRequis.builder()
                .typeDocument(request.typeDocument())
                .required(request.required())
                .niveauRequis(request.studentNiveau())
                .description(request.description())
                .build();
    }

    public DocumentRequisResponse toResponse(DocumentRequis entity) {
        return DocumentRequisResponse.builder()
                .trackingId(entity.getTrackingId())
                .typeDocument(entity.getTypeDocument())
                .required(entity.isRequired())
                .studentNiveau(entity.getNiveauRequis())
                .description(entity.getDescription())
                .build();
    }
}
