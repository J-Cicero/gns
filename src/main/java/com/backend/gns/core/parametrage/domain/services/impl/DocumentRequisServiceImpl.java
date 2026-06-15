package com.backend.gns.core.domain.services.impl;

import com.backend.gns.core.domain.models.DocumentRequis;
import com.backend.gns.core.domain.services.DocumentRequisService;
import com.backend.gns.core.infrastructure.repositories.DocumentRequisRepository;
import com.backend.gns.core.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.core.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.core.application.mappers.DocumentRequisMapper;
import com.backend.gns.student.domain.enums.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentRequisServiceImpl implements DocumentRequisService {

  private final DocumentRequisRepository repository;
  private final DocumentRequisMapper mapper;

  @Override
  @Transactional
  public DocumentRequisResponse create(DocumentRequisRequest request) {
    DocumentRequis entity = mapper.toEntity(request);
    return mapper.toResponse(repository.save(entity));
  }

  @Override
  public Optional<DocumentRequisResponse> findByTrackingId(UUID trackingId) {
    return repository.findByTrackingId(trackingId).map(mapper::toResponse);
  }

  @Override
  public Page<DocumentRequisResponse> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(mapper::toResponse);
  }

  @Override
  public Page<DocumentRequisResponse> findByTargetType(TargetType targetType, Pageable pageable) {
    // Méthode à ajouter dans le repository si nécessaire
    return repository.findAll(pageable).map(mapper::toResponse); // Simulé pour l'instant
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    repository.findByTrackingId(trackingId).ifPresent(repository::delete);
  }
}
