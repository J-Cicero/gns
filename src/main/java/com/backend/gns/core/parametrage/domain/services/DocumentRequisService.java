package com.backend.gns.core.parametrage.domain.services;

import com.backend.gns.core.parametrage.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.core.parametrage.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.core.parametrage.domain.enums.TypeDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRequisService {
    DocumentRequisResponse create(DocumentRequisRequest request);
    Optional<DocumentRequisResponse> findByTrackingId(UUID trackingId);
    List<DocumentRequisResponse> findAll();
    void delete(UUID trackingId);
}
