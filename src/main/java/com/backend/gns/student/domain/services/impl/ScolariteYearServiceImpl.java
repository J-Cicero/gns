package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.application.dtos.requests.ScolariteYearRequest;
import com.backend.gns.student.application.dtos.responses.ScolariteYearResponse;
import com.backend.gns.student.application.mappers.ScolariteYearMapper;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.services.ScolariteYearService;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScolariteYearServiceImpl implements ScolariteYearService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final ScolariteYearRepository scolariteYearRepository;
  private final ScolariteYearMapper scolariteYearMapper;

  public ScolariteYearServiceImpl(
      ScolariteYearRepository scolariteYearRepository, ScolariteYearMapper scolariteYearMapper) {
    this.scolariteYearRepository = scolariteYearRepository;
    this.scolariteYearMapper = scolariteYearMapper;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public ScolariteYearResponse create(ScolariteYearRequest request) {
    if (request.estOuverte()) {
      Optional<ScolariteYear> activeYear = scolariteYearRepository.findByEstOuverteTrue();
      if (activeYear.isPresent()) {
        throw new IllegalStateException("Une année scolaire est déjà ouverte. Veuillez la clôturer d'abord.");
      }
    }
    
    ScolariteYear entity = scolariteYearMapper.toEntity(request);
    entity.setTrackingId(UUID.randomUUID());
    ScolariteYear saved = scolariteYearRepository.save(entity);
    return scolariteYearMapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ScolariteYearResponse> findByTrackingId(UUID trackingId) {
    return scolariteYearRepository.findByTrackingId(trackingId).map(scolariteYearMapper::toResponse);
  }

  @Override
  @Transactional
  public ScolariteYearResponse cloturerEtOuvrirNouvelle(UUID oldTrackingId, ScolariteYearRequest newYearRequest) {
    ScolariteYear oldYear = scolariteYearRepository.findByTrackingId(oldTrackingId)
        .orElseThrow(() -> new EntityNotFoundException("Année scolaire non trouvée avec l'ID: " + oldTrackingId));
    
    oldYear.setEstOuverte(false);
    oldYear.setEstCloturee(true);
    scolariteYearRepository.save(oldYear);

    // On crée la nouvelle
    ScolariteYear newYear = scolariteYearMapper.toEntity(newYearRequest);
    newYear.setTrackingId(UUID.randomUUID());
    newYear.setEstOuverte(true);
    newYear.setEstCloturee(false);
    
    ScolariteYear savedNewYear = scolariteYearRepository.save(newYear);
    return scolariteYearMapper.toResponse(savedNewYear);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ScolariteYearResponse> findAll(Pageable pageable) {
    return scolariteYearRepository.findAll(normalize(pageable)).map(scolariteYearMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ScolariteYearResponse> findActiveYear() {
    return scolariteYearRepository.findByEstOuverteTrue().map(scolariteYearMapper::toResponse);
  }
}
