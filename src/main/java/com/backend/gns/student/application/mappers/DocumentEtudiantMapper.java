package com.backend.gns.student.application.mappers;

import com.backend.gns.commerce.domain.models.DocumentMerchant;
import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import com.backend.gns.student.domain.models.DocumentEtudiant;
import org.springframework.stereotype.Component;

@Component
public class DocumentEtudiantMapper {

  public DocumentResponse toResponse(DocumentEtudiant entity) {
    if (entity == null) return null;

    return DocumentResponse.builder()
        .trackingId(entity.getTrackingId())
        .ownerTrackingId(entity.getStudent() != null ? entity.getStudent().getTrackingId() : null)
        .documentType(entity.getDocumentType())
        .fileUrl(entity.getFileUrl())
        .providerPublicId(entity.getProviderPublicId())
        .status(entity.getStatus())
        .uploadedAt(entity.getUploadedAt())
        .build();
  }

  public DocumentResponse toResponse(DocumentMerchant entity) {
    if (entity == null) return null;

    return DocumentResponse.builder()
        .trackingId(entity.getTrackingId())
        .ownerTrackingId(entity.getMerchant() != null ? entity.getMerchant().getTrackingId() : null)
        .documentType(entity.getDocumentType())
        .fileUrl(entity.getFileUrl())
        .status(entity.getStatus())
        .uploadedAt(entity.getUploadedAt())
        .build();
  }
}
