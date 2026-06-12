package com.backend.gns.commerce.domain.services;

import com.backend.gns.core.domain.enums.TypeDocument;
import com.backend.gns.student.application.dtos.responses.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.util.UUID;

public interface DocumentMerchantService {
    DocumentResponse uploadDocument(MultipartFile fichier, UUID merchantTrackingId, TypeDocument typeDocument);
    Optional<DocumentResponse> findByTrackingId(UUID trackingId);
    Page<DocumentResponse> findByMerchantTrackingId(UUID merchantTrackingId, Pageable pageable);
    void delete(UUID trackingId);
}
