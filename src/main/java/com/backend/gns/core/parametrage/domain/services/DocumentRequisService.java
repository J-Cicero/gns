package com.backend.gns.core.domain.services;

import com.backend.gns.core.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.core.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.student.domain.enums.TargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRequisService {
  DocumentRequisResponse create(DocumentRequisRequest request);
  Optional<DocumentRequisResponse> findByTrackingId(UUID trackingId);
  Page<DocumentRequisResponse> findAll(Pageable pageable);
  Page<DocumentRequisResponse> findByTargetType(TargetType targetType, Pageable pageable);
  void delete(UUID trackingId);
}
