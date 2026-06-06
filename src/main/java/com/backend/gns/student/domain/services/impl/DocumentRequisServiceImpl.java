package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.application.dtos.requests.DocumentRequisRequest;
import com.backend.gns.student.application.dtos.responses.DocumentRequisResponse;
import com.backend.gns.student.application.mappers.DocumentRequisMapper;
import com.backend.gns.student.domain.enums.StudentNiveau;
import com.backend.gns.student.domain.models.DocumentRequis;
import com.backend.gns.student.domain.services.DocumentRequisService;
import com.backend.gns.student.infrastructure.repositories.DocumentRequisRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentRequisServiceImpl implements DocumentRequisService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final DocumentRequisRepository documentRequisRepository;
  private final DocumentRequisMapper documentRequisMapper;

  public DocumentRequisServiceImpl(
      DocumentRequisRepository documentRequisRepository,
      DocumentRequisMapper documentRequisMapper) {
    this.documentRequisRepository = documentRequisRepository;
    this.documentRequisMapper = documentRequisMapper;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public DocumentRequisResponse create(DocumentRequisRequest request) {
    if (documentRequisRepository.existsByNiveauAndTypeDocument(
        request.niveau(), request.typeDocument())) {
      throw new IllegalStateException("Ce document est déjà requis pour ce niveau.");
    }
    DocumentRequis entity = documentRequisMapper.toEntity(request);
    DocumentRequis saved = documentRequisRepository.save(entity);
    return documentRequisMapper.toResponse(saved);
  }

  @Override
  @Transactional
  public DocumentRequisResponse update(Long id, DocumentRequisRequest request) {
    DocumentRequis entity =
        documentRequisRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Règle non trouvée avec l'ID: " + id));

    entity.setNiveau(request.niveau());
    entity.setTypeDocument(request.typeDocument());
    entity.setObligatoire(request.obligatoire());
    entity.setEstActif(request.estActif());

    DocumentRequis updated = documentRequisRepository.save(entity);
    return documentRequisMapper.toResponse(updated);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    DocumentRequis entity =
        documentRequisRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Règle non trouvée avec l'ID: " + id));
    documentRequisRepository.delete(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public DocumentRequisResponse findById(Long id) {
    return documentRequisRepository
        .findById(id)
        .map(documentRequisMapper::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Règle non trouvée avec l'ID: " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<DocumentRequisResponse> findActiveByNiveau(StudentNiveau niveau) {
    return documentRequisRepository.findByNiveauAndEstActifTrue(niveau).stream()
        .map(documentRequisMapper::toResponse)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DocumentRequisResponse> findAll(Pageable pageable) {
    return documentRequisRepository
        .findAll(normalize(pageable))
        .map(documentRequisMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DocumentRequisResponse> findByNiveau(StudentNiveau niveau, Pageable pageable) {
    return documentRequisRepository
        .findByNiveau(niveau, normalize(pageable))
        .map(documentRequisMapper::toResponse);
  }
}
