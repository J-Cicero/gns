package com.backend.gns.student.domain.services.impl;

import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.student.application.dtos.requests.UniversiteRequest;
import com.backend.gns.student.application.dtos.responses.UniversiteResponse;
import com.backend.gns.student.application.mappers.UniversiteMapper;
import com.backend.gns.student.domain.models.Universite;
import com.backend.gns.student.domain.services.UniversiteService;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversiteServiceImpl implements UniversiteService {

  private final UniversiteRepository repository;
  private final UniversiteMapper mapper;
  private final StudentRepository studentRepository;

  @Override
  @Transactional
  public UniversiteResponse create(UniversiteRequest request) {
    if (request.code() != null && repository.findByCode(request.code().trim().toUpperCase()).isPresent()) {
      throw new IllegalArgumentException("Une université avec le code '" + request.code() + "' existe déjà.");
    }
    Universite entity = mapper.toEntity(request);
    if (entity.getCode() != null) {
      entity.setCode(entity.getCode().trim().toUpperCase());
    }
    return mapper.toResponse(repository.save(entity));
  }

  @Override
  @Transactional
  public UniversiteResponse update(UUID trackingId, UniversiteRequest request) {
    Universite entity =
        repository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new RuntimeException("Université non trouvée"));

    if (request.code() != null && !request.code().trim().equalsIgnoreCase(entity.getCode())) {
      if (repository.findByCode(request.code().trim().toUpperCase()).isPresent()) {
        throw new IllegalArgumentException("Une université avec le code '" + request.code() + "' existe déjà.");
      }
      entity.setCode(request.code().trim().toUpperCase());
    }
    entity.setFullName(request.fullName());
    entity.setCity(request.city());
    entity.setActive(request.isActive());
    return mapper.toResponse(repository.save(entity));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UniversiteResponse> findByTrackingId(UUID trackingId) {
    return repository.findByTrackingId(trackingId).map(mapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UniversiteResponse> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(mapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UniversiteResponse> findActives(Pageable pageable) {
    return repository.findByIsActiveTrue(pageable).map(mapper::toResponse);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Universite entity =
        repository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new RuntimeException("Université non trouvée"));
    
    if (studentRepository.countByUniversite(entity) > 0) {
        throw new IllegalStateException("Impossible de supprimer cette université : des étudiants y sont rattachés.");
    }
    
    repository.delete(entity);
  }

  @Override
  @Transactional
  public UniversiteResponse updateEtat(UUID trackingId, boolean etat) {
    Universite entity =
        repository
            .findByTrackingId(trackingId)
            .orElseThrow(() -> new RuntimeException("Université non trouvée"));
    entity.setActive(etat);
    return mapper.toResponse(repository.save(entity));
  }

  @Override
  public List<Map<String, Object>> getSummaryStats() {
    return repository.findAll().stream()
        .map(
            u -> {
              Map<String, Object> map = new HashMap<>();
              map.put("trackingId", u.getTrackingId());
              map.put("nom", u.getFullName());
              map.put("code", u.getCode());
              map.put("nbEtudiants", studentRepository.countByUniversite(u));
              map.put(
                  "nbEligibles",
                  studentRepository.countByUniversiteAndKycStatus(
                      u, KycStatus.VALIDE));
              return map;
            })
        .collect(Collectors.toList());
  }
}
