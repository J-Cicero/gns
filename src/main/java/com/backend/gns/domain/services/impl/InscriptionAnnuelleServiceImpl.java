package com.backend.gns.domain.services.impl;

import com.backend.gns.application.dtos.requests.InscriptionAnnuelleRequest;
import com.backend.gns.application.dtos.responses.InscriptionAnnuelleResponse;
import com.backend.gns.application.mappers.InscriptionAnnuelleMapper;
import com.backend.gns.domain.enums.StatutInscription;
import com.backend.gns.domain.models.InscriptionAnnuelle;
import com.backend.gns.domain.services.InscriptionAnnuelleService;
import com.backend.gns.infrastructure.repositories.InscriptionAnnuelleRepository;
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
public class InscriptionAnnuelleServiceImpl implements InscriptionAnnuelleService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final InscriptionAnnuelleRepository inscriptionRepository;
  private final InscriptionAnnuelleMapper inscriptionMapper;

  public InscriptionAnnuelleServiceImpl(
      InscriptionAnnuelleRepository inscriptionRepository,
      InscriptionAnnuelleMapper inscriptionMapper) {
    this.inscriptionRepository = inscriptionRepository;
    this.inscriptionMapper = inscriptionMapper;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse create(InscriptionAnnuelleRequest request) {
    InscriptionAnnuelle inscription = inscriptionMapper.toEntity(request);
    inscription.setDateActivation(LocalDateTime.now());
    InscriptionAnnuelle savedInscription = inscriptionRepository.save(inscription);
    return inscriptionMapper.toResponse(savedInscription);
  }

  @Override
  public Optional<InscriptionAnnuelleResponse> findByTrackingId(UUID trackingId) {
    return inscriptionRepository
        .findByTrackingId(trackingId)
        .map(inscriptionMapper::toResponse);
  }

  @Override
  @Transactional
  public InscriptionAnnuelleResponse update(
      UUID trackingId, InscriptionAnnuelleRequest request) {
    InscriptionAnnuelle inscription =
        inscriptionRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Inscription non trouvée avec trackingId: " + trackingId));

    inscription.setAnneeAcademique(request.anneeAcademique());
    inscription.setNiveau(request.niveau());
    inscription.setCreditsTotalValides(request.creditsTotalValides());
    inscription.setEstBoursier(request.estBoursier());
    inscription.setTypeBourse(request.typeBourse());
    inscription.setFraisScolaritePayes(request.fraisScolaritePayes());
    inscription.setStatut(request.statut());
    inscription.setSource(request.source());

    InscriptionAnnuelle updatedInscription = inscriptionRepository.save(inscription);
    return inscriptionMapper.toResponse(updatedInscription);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    InscriptionAnnuelle inscription =
        inscriptionRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Inscription non trouvée avec trackingId: " + trackingId));
    inscriptionRepository.delete(inscription);
  }

  @Override
  public Page<InscriptionAnnuelleResponse> findByStudentTrackingId(
      UUID studentTrackingId, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return inscriptionRepository
        .findByStudentTrackingId(studentTrackingId, normalized)
        .map(inscriptionMapper::toResponse);
  }

  @Override
  public Optional<InscriptionAnnuelleResponse> findByStudentAndAnnee(
      UUID studentTrackingId, String anneeAcademique) {
    return inscriptionRepository
        .findByStudentAndAnnee(studentTrackingId, anneeAcademique)
        .map(inscriptionMapper::toResponse);
  }

  @Override
  public Page<InscriptionAnnuelleResponse> findByStatut(
      StatutInscription statut, Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return inscriptionRepository
        .findByStatut(statut, normalized)
        .map(inscriptionMapper::toResponse);
  }

  @Override
  public Page<InscriptionAnnuelleResponse> findAll(Pageable pageable) {
    Pageable normalized = normalize(pageable);
    return inscriptionRepository
        .findAll(normalized)
        .map(inscriptionMapper::toResponse);
  }
}
