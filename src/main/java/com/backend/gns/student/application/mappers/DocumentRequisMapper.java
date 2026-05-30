package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.student.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.student.domain.models.DocumentRequis;
import org.springframework.stereotype.Component;

@Component
public class DocumentRequisMapper {

  public DocumentRequis toEntity(DocumentRequisRequest request) {
    if (request == null) {
      return null;
    }
    DocumentRequis entity = new DocumentRequis();
    entity.setNiveau(request.niveau());
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
        entity.getNiveau(),
        entity.getTypeDocument(),
        entity.isObligatoire(),
        entity.isEstActif()
    );
  }
}
