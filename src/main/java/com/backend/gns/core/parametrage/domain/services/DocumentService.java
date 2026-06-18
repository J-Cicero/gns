package com.backend.gns.core.parametrage.domain.services;

import com.backend.gns.core.parametrage.application.dtos.responses.DocumentResponse;
import com.backend.gns.core.parametrage.domain.enums.ProprietaireType;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentResponse uploadDocument(MultipartFile file, UUID ownerTrackingId, ProprietaireType ownerType, TypeDocument documentType);
    List<DocumentResponse> getDocumentsByOwner(UUID ownerTrackingId);
    DocumentResponse getDocumentByTrackingId(UUID trackingId);
    void deleteDocument(UUID trackingId);
}
