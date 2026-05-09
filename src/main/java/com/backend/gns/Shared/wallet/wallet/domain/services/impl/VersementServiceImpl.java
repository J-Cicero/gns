package com.backend.gns.Shared.wallet.domain.services.impl;

import com.backend.gns.Shared.wallet.application.dtos.requests.VersementRequest;
import com.backend.gns.Shared.wallet.application.dtos.responses.VersementResponse;
import com.backend.gns.Shared.wallet.application.mappers.VersementMapper;
import com.backend.gns.Shared.wallet.domain.enums.VersementStatut;
import com.backend.gns.Shared.wallet.domain.enums.VersementType;
import com.backend.gns.admin.domain.models.Admin;
import com.backend.gns.Shared.wallet.domain.models.Versement;
import com.backend.gns.Shared.wallet.domain.services.VersementService;
import com.backend.gns.Shared.wallet.domain.services.WalletService;
import com.backend.gns.admin.infrastructure.repositories.AdminRepository;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.Shared.wallet.infrastructure.repositories.VersementRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VersementServiceImpl implements VersementService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final VersementRepository versementRepository;
  private final VersementMapper versementMapper;
  private final StudentRepository studentRepository;
  private final BoutiqueRepository boutiqueRepository;
  private final WalletService walletService;
  private final AdminRepository adminRepository;

  public VersementServiceImpl(
      VersementRepository versementRepository,
      VersementMapper versementMapper,
      StudentRepository studentRepository,
      BoutiqueRepository boutiqueRepository,
      WalletService walletService,
      AdminRepository adminRepository) {
    this.versementRepository = versementRepository;
    this.versementMapper = versementMapper;
    this.studentRepository = studentRepository;
    this.boutiqueRepository = boutiqueRepository;
    this.walletService = walletService;
    this.adminRepository = adminRepository;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public VersementResponse create(VersementRequest request) {
    Versement versement = versementMapper.toEntity(request);
    Versement savedVersement = versementRepository.save(versement);
    return versementMapper.toResponse(savedVersement);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<VersementResponse> findByTrackingId(UUID trackingId) {
    return versementRepository.findByTrackingId(trackingId).map(versementMapper::toResponse);
  }

  @Override
  @Transactional
  public VersementResponse update(UUID trackingId, VersementRequest request) {
    Versement versement =
        versementRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Versement non trouvé avec l'ID: " + trackingId));

    versement.setMontantVerse(request.montantVerse());
    versement.setTypeVersement(request.typeVersement());
    versement.setDateVersement(
        request.dateVersement() != null ? request.dateVersement() : LocalDateTime.now());
    versement.setStatut(request.statut());

    Versement updatedVersement = versementRepository.save(versement);
    return versementMapper.toResponse(updatedVersement);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Versement versement =
        versementRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Versement non trouvé avec l'ID: " + trackingId));
    versementRepository.delete(versement);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<VersementResponse> findByStatut(VersementStatut statut, Pageable pageable) {
    return versementRepository
        .findByStatut(statut, normalize(pageable))
        .map(versementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<VersementResponse> findByTypeVersement(
      VersementType typeVersement, Pageable pageable) {
    return versementRepository
        .findByTypeVersement(typeVersement, normalize(pageable))
        .map(versementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<VersementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable) {
    return versementRepository
        .findByWalletTrackingId(walletTrackingId, normalize(pageable))
        .map(versementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<VersementResponse> findAll(Pageable pageable) {
    return versementRepository.findAll(normalize(pageable)).map(versementMapper::toResponse);
  }

  @Override
  @Transactional
  public void versementAusTousEtudiants(BigDecimal montant, String description) {
    var students = studentRepository.findAll(Pageable.unpaged());

    for (var student : students.getContent()) {
      if (student.getWallet() != null) {
        Versement versement = new Versement();
        versement.setTrackingId(UUID.randomUUID());
        versement.setWallet(student.getWallet());
        versement.setMontantVerse(montant);
        versement.setTypeVersement(VersementType.BOURSE_DBS_36k);
        versement.setStatut(VersementStatut.VALIDEE);
        versement.setDateVersement(LocalDateTime.now());
        versementRepository.save(versement);

        // Créditer le wallet
        walletService.crediter(student.getWallet().getTrackingId(), montant);
      }
    }
  }

  @Override
  @Transactional
  public void versementAusToutesBoutiques(BigDecimal montant, String description) {
    var boutiques = boutiqueRepository.findAll(Pageable.unpaged());

    for (var boutique : boutiques.getContent()) {
      if (boutique.getWallet() != null) {
        Versement versement = new Versement();
        versement.setTrackingId(UUID.randomUUID());
        versement.setWallet(boutique.getWallet());
        versement.setMontantVerse(montant);
        versement.setTypeVersement(VersementType.MERCHANT);
        versement.setStatut(VersementStatut.VALIDEE);
        versement.setDateVersement(LocalDateTime.now());
        versementRepository.save(versement);

        // Créditer le wallet
        walletService.crediter(boutique.getWallet().getTrackingId(), montant);
      }
    }
  }
}
