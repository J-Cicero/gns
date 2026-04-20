package com.backend.gns.domain.services.impl;

import com.backend.gns.application.dtos.requests.DocumentEtudiantRequest;
import com.backend.gns.application.dtos.responses.DocumentEtudiantResponse;
import com.backend.gns.application.mappers.DocumentEtudiantMapper;
import com.backend.gns.domain.enums.StatutDocument;
import com.backend.gns.domain.enums.TypeDocument;
import com.backend.gns.domain.models.DocumentEtudiant;
import com.backend.gns.domain.services.DocumentEtudiantService;
import com.backend.gns.infrastructure.repositories.DocumentEtudiantRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentEtudiantServiceImpl implements DocumentEtudiantService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final DocumentEtudiantRepository documentRepository;
  private final DocumentEtudiantMapper documentMapper;

  public DocumentEtudiantServiceImpl(
      DocumentEtudiantRepository documentRepository, DocumentEtudiantMapper documentMapper) {
    this.documentRepository = documentRepository;
    this.documentMapper = documentMapper;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public DocumentEtudiantResponse create(DocumentEtudiantRequest request) {
    DocumentEtudiant document = documentMapper.toEntity(request);
    document.setDateDepot(LocalDateTime.now());
    document.setStatut(StatutDocument.EN_ATTENTE);
    DocumentEtudiant savedDocument = documentRepository.save(document);
    return documentMapper.toResponse(savedDocument);
  }

  @Override
  public Optional<DocumentEtudiantResponse> findByTrackingId(UUID trackingId) {
    return documentRepository
        .findByTrackingId(trackingId)
        .map(documentMapper::toResponse);
  }

  @Override
  @Transactional
  public DocumentEtudiantResponse update(UUID trackingId, DocumentEtudiantRequest request) {
    DocumentEtudiant document =
        documentRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Document non trouvé avec trackingId: " + trackingId));

    document.setType(request.type());
    document.setCheminFichier(request.cheminFichier());

    DocumentEtudiant updatedDocument = documentRepository.save(document);
    return documentMapper.toResponse(updatedDocument);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    DocumentEtudiant document =
        documentRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Document non trouvé avec trackingId: " + trackingId));
    documentRepository.delete(document);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStudentTrackingId(
      UUID studentTrackingId, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository
        .findByStudentTrackingId(studentTrackingId, normalized)
        .map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByInscriptionTrackingId(
      UUID inscriptionTrackingId, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository
        .findByInscriptionTrackingId(inscriptionTrackingId, normalized)
        .map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStudentAndStatut(
      UUID studentTrackingId, StatutDocument statut, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository
        .findByStudentAndStatut(studentTrackingId, statut, normalized)
        .map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findByStatut(StatutDocument statut, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository
        .findByStatut(statut, normalized)
        .map(documentMapper::toResponse);
  }

  @Override
  public Page<DocumentEtudiantResponse> findAll(Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return documentRepository
        .findAll(normalized)
        .map(documentMapper::toResponse);
  }
}
