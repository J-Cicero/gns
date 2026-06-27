package com.backend.gns.student.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.student.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import org.springframework.stereotype.Component;
@Component
public class DocumentEtudiantMapper {

  public DocumentResponse toResponse(DocumentEtudiant entity) {
    return DocumentResponse.builder()
            .trackingId(entity.getTrackingId())
            .ownerTrackingId(entity.getStudent().getTrackingId())
            .documentType(entity.getDocumentType())
            .fileUrl(entity.getFileUrl())
            .status(entity.getStatus())
            .rejectionReason(entity.getRejectionReason())
            .uploadedAt(entity.getUploadedAt())
            .ownerType(ProprietaireType.STUDENT)
            .build();
  }

  // Pour l'API spécifique (Étudiant Mobile)
  public DocumentEtudiantResponse toEtudiantResponse(DocumentEtudiant entity) {
    return new DocumentEtudiantResponse(
            entity.getTrackingId(),
            entity.getDocumentType(),
            entity.getFileUrl(),
            entity.getStatus(),
            entity.getUploadedAt(),
            entity.getRejectionReason(),
            entity.getInscription() != null ? entity.getInscription().getTrackingId() : null,
            "ETUDIANT"
    );
  }
}