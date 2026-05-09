package com.backend.gns.domain.services.impl;

import com.backend.gns.application.dtos.requests.BanqueEtudiantRequest;
import com.backend.gns.application.dtos.responses.BanqueEtudiantResponse;
import com.backend.gns.application.mappers.BanqueEtudiantMapper;
import com.backend.gns.domain.exception.ResourceNotFoundException;
import com.backend.gns.domain.models.BanqueEtudiant;
import com.backend.gns.domain.services.BanqueEtudiantService;
import com.backend.gns.infrastructure.repositories.BanqueEtudiantRepository;
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
public class BanqueEtudiantServiceImpl implements BanqueEtudiantService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final BanqueEtudiantRepository banqueEtudiantRepository;
  private final BanqueEtudiantMapper banqueEtudiantMapper;

  // ─────────────────────────────────────────────
  // Utilitaires privés
  // ─────────────────────────────────────────────

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private BanqueEtudiant findBanqueEtudiantOrThrow(UUID trackingId) {
    return banqueEtudiantRepository
        .findByTrackingId(trackingId)
        .orElseThrow(() -> {
          log.warn("BanqueEtudiant introuvable avec trackingId: {}", trackingId);
          return new ResourceNotFoundException(
              "BanqueEtudiant non trouvée avec l'ID: " + trackingId);
        });
  }

  @Override
  @Transactional
  public BanqueEtudiantResponse create(BanqueEtudiantRequest request) {
    log.info("Création d'une BanqueEtudiant, banque: {}", request.banque());

    BanqueEtudiant banqueEtudiant = banqueEtudiantMapper.toEntity(request);
    BanqueEtudiant saved = banqueEtudiantRepository.save(banqueEtudiant);

    log.info("BanqueEtudiant créée avec succès, trackingId: {}", saved.getTrackingId());
    return banqueEtudiantMapper.toResponse(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<BanqueEtudiantResponse> findByTrackingId(UUID trackingId) {
    log.debug("Recherche BanqueEtudiant par trackingId: {}", trackingId);
    return banqueEtudiantRepository
        .findByTrackingId(trackingId)
        .map(banqueEtudiantMapper::toResponse);
  }

  @Override
  @Transactional
  public BanqueEtudiantResponse update(UUID trackingId, BanqueEtudiantRequest request) {
    log.info("Mise à jour BanqueEtudiant trackingId: {}", trackingId);

    BanqueEtudiant banqueEtudiant = findBanqueEtudiantOrThrow(trackingId);

    banqueEtudiant.setBanque(request.banque());
    banqueEtudiant.setRIB(request.RIB());
    banqueEtudiant.setMandatStatut(request.mandatStatut());

    BanqueEtudiant updated = banqueEtudiantRepository.save(banqueEtudiant);
    log.info("BanqueEtudiant mise à jour avec succès, trackingId: {}", trackingId);
    return banqueEtudiantMapper.toResponse(updated);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    log.info("Suppression BanqueEtudiant trackingId: {}", trackingId);
    BanqueEtudiant banqueEtudiant = findBanqueEtudiantOrThrow(trackingId);
    banqueEtudiantRepository.delete(banqueEtudiant);
    log.info("BanqueEtudiant supprimée avec succès, trackingId: {}", trackingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BanqueEtudiantResponse> findAll(Pageable pageable) {
    log.debug("Récupération de toutes les BanqueEtudiant, page: {}", pageable.getPageNumber());
    return banqueEtudiantRepository.findAll(normalize(pageable)).map(banqueEtudiantMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BanqueEtudiantResponse> findByStudentTrackingId(UUID studentTrackingId, Pageable pageable) {
    log.debug("Recherche BanqueEtudiant par studentTrackingId: {}", studentTrackingId);
    return banqueEtudiantRepository
        .findByStudentTrackingId(studentTrackingId, normalize(pageable))
        .map(banqueEtudiantMapper::toResponse);
  }
}