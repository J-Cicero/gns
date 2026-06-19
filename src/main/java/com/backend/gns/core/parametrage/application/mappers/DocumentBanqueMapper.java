package com.backend.gns.core.parametrage.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.responses.DocumentBanqueResponse;
import com.backend.gns.core.parametrage.domain.models.DocumentBanque;
import org.springframework.stereotype.Component;

@Component
public class DocumentBanqueMapper {

    public DocumentBanqueResponse toResponse(DocumentBanque entity) {
        if (entity == null) {
            return null;
        }

        return DocumentBanqueResponse.builder()
                .trackingId(entity.getTrackingId())
                .documentType(entity.getDocumentType())
                .fileUrl(entity.getFileUrl())
                .status(entity.getStatus())
                .uploadedAt(entity.getUploadedAt())

                .compteBancaireTrackingId(
                        entity.getCompteBancaire() != null ? entity.getCompteBancaire().getTrackingId() : null
                )
                .build();
    }
}