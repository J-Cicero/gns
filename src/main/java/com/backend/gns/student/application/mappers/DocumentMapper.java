package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import com.backend.gns.commerce.domain.models.DocumentMerchant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

  public DocumentResponse toResponse(DocumentEtudiant entity) {
    if (entity == null) return null;

    return DocumentResponse.builder()
        .trackingId(entity.getTrackingId())
        .inscriptionId(entity.getInscription() != null ? entity.getInscription().getTrackingId() : null)
        .type(entity.getType())
        .cheminFichier(entity.getUrlFichier())
        .statut(entity.getStatut())
        .dateDepot(entity.getDateDepot())
        .build();
  }

  public DocumentResponse toResponse(DocumentMerchant entity) {
    if (entity == null) return null;

    return DocumentResponse.builder()
        .trackingId(entity.getTrackingId())
        .userTrackingId(entity.getMerchant() != null ? entity.getMerchant().getTrackingId() : null)
        .type(entity.getType())
        .cheminFichier(entity.getUrlFichier())
        .statut(entity.getStatut())
        .dateDepot(entity.getDateDepot())
        .build();
  }
}
