package com.backend.gns.admin.domain.services.impl;

import com.backend.gns.admin.application.dtos.requests.UniversityAdminRequest;
import com.backend.gns.admin.application.dtos.responses.UniversityAdminResponse;
import com.backend.gns.admin.application.mappers.UniversityAdminMapper;
import com.backend.gns.Shared.exception.ResourceNotFoundException;
import com.backend.gns.Shared.user.domain.models.UniversityAdmin;
import com.backend.gns.admin.domain.services.UniversityAdminService;
import com.backend.gns.admin.infrastructure.repositories.UniversityAdminRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniversityAdminServiceImpl implements UniversityAdminService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final UniversityAdminRepository universityAdminRepository;
  private final UniversityAdminMapper universityAdminMapper;

  // ─────────────────────────────────────────────
  // Utilitaires privés
  // ─────────────────────────────────────────────

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private UniversityAdmin findUniversityAdminOrThrow(UUID trackingId) {
    return universityAdminRepository
        .findByTrackingId(trackingId)
        .orElseThrow(() -> {
          log.warn("UniversityAdmin introuvable avec trackingId: {}", trackingId);
          return new ResourceNotFoundException("UniversityAdmin non trouvé avec l'ID: " + trackingId);
        });
  }

  @Override
  @Transactional
  public UniversityAdminResponse create(UniversityAdminRequest request) {
    log.info("Création d'un UniversityAdmin: {} {}", request.prenom(), request.nom());

    UniversityAdmin universityAdmin = universityAdminMapper.toEntity(request);
    UniversityAdmin savedUniversityAdmin = universityAdminRepository.save(universityAdmin);

    log.info("UniversityAdmin créé avec succès, trackingId: {}", savedUniversityAdmin.getTrackingId());
    return universityAdminMapper.toResponse(savedUniversityAdmin);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UniversityAdminResponse> findByTrackingId(UUID trackingId) {
    log.debug("Recherche UniversityAdmin par trackingId: {}", trackingId);
    return universityAdminRepository.findByTrackingId(trackingId).map(universityAdminMapper::toResponse);
  }

  @Override
  @Transactional
  public UniversityAdminResponse update(UUID trackingId, UniversityAdminRequest request) {
    log.info("Mise à jour UniversityAdmin trackingId: {}", trackingId);

    UniversityAdmin universityAdmin = findUniversityAdminOrThrow(trackingId);

    universityAdmin.setEmail(request.email());
    universityAdmin.setNom(request.nom());
    universityAdmin.setPrenom(request.prenom());
    universityAdmin.setTelephone(request.telephone());
    universityAdmin.setNumeroCompte(request.numeroCompte());

    UniversityAdmin updatedUniversityAdmin = universityAdminRepository.save(universityAdmin);
    log.info("UniversityAdmin mis à jour avec succès, trackingId: {}", trackingId);
    return universityAdminMapper.toResponse(updatedUniversityAdmin);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    log.info("Suppression UniversityAdmin trackingId: {}", trackingId);
    UniversityAdmin universityAdmin = findUniversityAdminOrThrow(trackingId);
    universityAdminRepository.delete(universityAdmin);
    log.info("UniversityAdmin supprimé avec succès, trackingId: {}", trackingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UniversityAdminResponse> findAll(Pageable pageable) {
    log.debug("Récupération de tous les UniversityAdmin, page: {}", pageable.getPageNumber());
    return universityAdminRepository.findAll(normalize(pageable)).map(universityAdminMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UniversityAdminResponse> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable) {
    log.debug("Recherche UniversityAdmin par université trackingId: {}", universiteTrackingId);
    return universityAdminRepository.findByUniversiteTrackingId(universiteTrackingId, normalize(pageable))
        .map(universityAdminMapper::toResponse);
  }
}
