package com.backend.gns.core.application.mappers;

import com.backend.gns.core.domain.models.DocumentRequis;
import com.backend.gns.core.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.core.application.dtos.responses.DocumentRequisResponse;
import org.springframework.stereotype.Component;

@Component
public class DocumentRequisMapper {

  public DocumentRequis toEntity(DocumentRequisRequest request) {
    if (request == null) {
      return null;
    }
    DocumentRequis entity = new DocumentRequis();
    entity.setNiveau(request.niveau());
    entity.setTargetType(request.targetType());
    entity.setTypeDocument(request.typeDocument());
    entity.setObligatoire(request.obligatoire());
    entity.setEstActif(request.estActif());
    return entity;
  }

  public DocumentRequisResponse toResponse(DocumentRequis entity) {
    if (entity == null) {
      return null;
    }
    return new DocumentRequisResponse(
        entity.getId(),
        entity.getTrackingId(),
        entity.getNiveau(),
        entity.getTargetType(),
        entity.getTypeDocument(),
        entity.isObligatoire(),
        entity.isEstActif());
  }
}
