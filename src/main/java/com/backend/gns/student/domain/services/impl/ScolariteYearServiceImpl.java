package com.backend.gns.student.domain.services.impl;

import com.backend.gns.student.application.dtos.requests.ScolariteYearRequest;
import com.backend.gns.student.application.dtos.responses.ScolariteYearResponse;
import com.backend.gns.student.application.mappers.ScolariteYearMapper;
import com.backend.gns.student.domain.models.ScolariteYear;
import com.backend.gns.student.domain.services.ScolariteYearService;
import com.backend.gns.student.infrastructure.repositories.ScolariteYearRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

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
    if (request.isOpen()) {
      Optional<ScolariteYear> activeYear = scolariteYearRepository.findByIsOpenTrue();
      if (activeYear.isPresent()) {
        throw new IllegalStateException(
            "Une année scolaire est déjà ouverte. Veuillez la clôturer d'abord.");
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
    return scolariteYearRepository
        .findByTrackingId(trackingId)
        .map(scolariteYearMapper::toResponse);
  }

  @Override
  @Transactional
  public void cloturer(UUID trackingId) {
    ScolariteYear year = scolariteYearRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new EntityNotFoundException("Année non trouvée"));
    
    year.setOpen(false);
    year.setClosed(true);
    scolariteYearRepository.save(year);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ScolariteYearResponse> findAll(Pageable pageable) {
    return scolariteYearRepository
        .findAll(normalize(pageable))
        .map(scolariteYearMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ScolariteYearResponse> findActiveYear() {
    return scolariteYearRepository.findByIsOpenTrue().map(scolariteYearMapper::toResponse);
  }
}
