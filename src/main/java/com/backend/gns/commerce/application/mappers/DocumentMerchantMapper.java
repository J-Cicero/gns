package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.domain.models.DocumentMerchant;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import org.springframework.stereotype.Component;

@Component
public class DocumentMerchantMapper {

    public com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse toResponse(DocumentMerchant entity) {
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
