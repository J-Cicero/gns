package com.backend.gns.admin.domain.services.impl;

import com.backend.gns.admin.application.dtos.requests.AdminRequest;
import com.backend.gns.admin.application.dtos.responses.AdminResponse;
import com.backend.gns.admin.application.mappers.AdminMapper;
import com.backend.gns.admin.domain.models.Admin;
import com.backend.gns.admin.domain.services.AdminService;
import com.backend.gns.admin.infrastructure.repositories.AdminRepository;
import com.backend.gns.core.exception.ResourceNotFoundException;

import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class AdminServiceImpl implements AdminService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final AdminRepository adminRepository;
  private final AdminMapper adminMapper;

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private Admin findAdminOrThrow(UUID trackingId) {
    return adminRepository
        .findByTrackingId(trackingId)
        .orElseThrow(
            () -> {
              log.warn("Admin introuvable avec trackingId: {}", trackingId);
              return new ResourceNotFoundException("Admin non trouvé avec l'ID: " + trackingId);
            });
  }

  @Override
  @Transactional
  public AdminResponse create(AdminRequest request) {
    log.info("Création d'un admin: {}", request.email());
    Admin admin = adminMapper.toEntity(request);

    // Initialisation du Wallet via Cascade
    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setTypeWallet(WalletType.ADMIN);
    wallet.setStatutWallet(WalletStatus.ACTIF);
    wallet.setSolde(BigDecimal.ZERO);

    wallet.setPlafond(BigDecimal.ZERO);
    wallet.setDateCreation(LocalDateTime.now());

    admin.setWallet(wallet);

    Admin savedAdmin = adminRepository.save(admin);
    log.info("Admin créé avec succès avec son Wallet, trackingId: {}", savedAdmin.getTrackingId());
    return adminMapper.toResponse(savedAdmin);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<AdminResponse> findByTrackingId(UUID trackingId) {
    log.debug("Recherche admin par trackingId: {}", trackingId);
    return adminRepository.findByTrackingId(trackingId).map(adminMapper::toResponse);
  }

  @Override
  @Transactional
  public AdminResponse update(UUID trackingId, AdminRequest request) {
    log.info("Mise à jour admin trackingId: {}", trackingId);

    Admin admin = findAdminOrThrow(trackingId);
    admin.setEmail(request.email());
    admin.setNom(request.nom());
    admin.setPrenom(request.prenom());
    admin.setTelephone(request.telephone());
    admin.setDateNaissance(request.dateNaissance());
    admin.setNumeroCompte(request.numeroCompte());

    Admin updatedAdmin = adminRepository.save(admin);
    log.info("Admin mis à jour avec succès, trackingId: {}", trackingId);
    return adminMapper.toResponse(updatedAdmin);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    log.info("Suppression admin trackingId: {}", trackingId);
    Admin admin = findAdminOrThrow(trackingId);
    adminRepository.delete(admin);
    log.info("Admin supprimé avec succès, trackingId: {}", trackingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<AdminResponse> findAll(Pageable pageable) {
    log.debug("Récupération de tous les admins, page: {}", pageable.getPageNumber());
    return adminRepository.findAll(normalize(pageable)).map(adminMapper::toResponse);
  }
}
