package com.backend.gns.core.parametrage.application.mappers;

import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.core.parametrage.domain.models.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getTrackingId(),
                document.getOwnerTrackingId(),
                document.getOwnerType(),
                document.getDocumentType(),
                document.getFileUrl(),
                document.getStatus(),
                document.getUploadedAt()
        );
    }

    // No toEntity from Request needed as creation involves MultipartFile upload,
    // where Document entity is built directly in the service.
}
