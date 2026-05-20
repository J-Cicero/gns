package com.backend.gns.admin.domain.services.impl;

import com.backend.gns.admin.application.dtos.requests.AdminULRequest;
import com.backend.gns.admin.application.dtos.responses.AdminULResponse;
import com.backend.gns.admin.application.mappers.AdminULMapper;
import com.backend.gns.Shared.exception.ResourceNotFoundException;
import com.backend.gns.admin.domain.models.AdminUL;
import com.backend.gns.admin.domain.services.AdminULService;
import com.backend.gns.admin.infrastructure.repositories.AdminULRepository;
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
public class AdminULServiceImpl implements AdminULService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final AdminULRepository adminULRepository;
  private final AdminULMapper adminULMapper;

  // ─────────────────────────────────────────────
  // Utilitaires privés
  // ─────────────────────────────────────────────

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private AdminUL findAdminULOrThrow(UUID trackingId) {
    return adminULRepository
        .findByTrackingId(trackingId)
        .orElseThrow(() -> {
          log.warn("AdminUL introuvable avec trackingId: {}", trackingId);
          return new ResourceNotFoundException("AdminUL non trouvé avec l'ID: " + trackingId);
        });
  }

  @Override
  @Transactional
  public AdminULResponse create(AdminULRequest request) {
    log.info("Création d'un AdminUL: {} {}", request.prenom(), request.nom());

    AdminUL adminUL = adminULMapper.toEntity(request);
    AdminUL savedAdminUL = adminULRepository.save(adminUL);

    log.info("AdminUL créé avec succès, trackingId: {}", savedAdminUL.getTrackingId());
    return adminULMapper.toResponse(savedAdminUL);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<AdminULResponse> findByTrackingId(UUID trackingId) {
    log.debug("Recherche AdminUL par trackingId: {}", trackingId);
    return adminULRepository.findByTrackingId(trackingId).map(adminULMapper::toResponse);
  }

  @Override
  @Transactional
  public AdminULResponse update(UUID trackingId, AdminULRequest request) {
    log.info("Mise à jour AdminUL trackingId: {}", trackingId);

    AdminUL adminUL = findAdminULOrThrow(trackingId);

    adminUL.setEmail(request.email());
    adminUL.setNom(request.nom());
    adminUL.setPrenom(request.prenom());
    adminUL.setTelephone(request.telephone());
    adminUL.setNumeroCompte(request.numeroCompte());

    AdminUL updatedAdminUL = adminULRepository.save(adminUL);
    log.info("AdminUL mis à jour avec succès, trackingId: {}", trackingId);
    return adminULMapper.toResponse(updatedAdminUL);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    log.info("Suppression AdminUL trackingId: {}", trackingId);
    AdminUL adminUL = findAdminULOrThrow(trackingId);
    adminULRepository.delete(adminUL);
    log.info("AdminUL supprimé avec succès, trackingId: {}", trackingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AdminULResponse> findAll(Pageable pageable) {
    log.debug("Récupération de tous les AdminUL, page: {}", pageable.getPageNumber());
    return adminULRepository.findAll(normalize(pageable)).map(adminULMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AdminULResponse> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable) {
    log.debug("Recherche AdminUL par université trackingId: {}", universiteTrackingId);
    return adminULRepository.findByUniversiteTrackingId(universiteTrackingId, normalize(pageable))
        .map(adminULMapper::toResponse);
  }
}
