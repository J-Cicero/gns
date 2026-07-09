package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.commerce.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.commerce.application.mappers.BoutiqueMapper;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.services.BoutiqueService;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoutiqueServiceImpl implements BoutiqueService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final BoutiqueRepository boutiqueRepository;
  private final BoutiqueMapper boutiqueMapper;
  private final MerchantRepository merchantRepository;
  private final WalletRepository walletRepository;
  private final ParametreGnsService parametreGnsService;
  private final CloudinaryStorageService cloudinaryService;

  public BoutiqueServiceImpl(
      BoutiqueRepository boutiqueRepository,
      BoutiqueMapper boutiqueMapper,
      MerchantRepository merchantRepository,
      WalletRepository walletRepository,
      ParametreGnsService parametreGnsService,
      CloudinaryStorageService cloudinaryService) {
    this.boutiqueRepository = boutiqueRepository;
    this.boutiqueMapper = boutiqueMapper;
    this.merchantRepository = merchantRepository;
    this.walletRepository = walletRepository;
    this.parametreGnsService = parametreGnsService;
    this.cloudinaryService = cloudinaryService;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void initMissingWalletsForBoutiques() {
    java.util.List<Boutique> boutiques = boutiqueRepository.findAll();
    for (Boutique boutique : boutiques) {
      if (boutique.getWallet() == null) {
        Wallet wallet = new Wallet();
        wallet.setTrackingId(UUID.randomUUID());
        wallet.setWalletType(WalletType.BOUTIQUE);
        wallet.setStatus(WalletStatus.ACTIF);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setLimitAmount(new BigDecimal("100000"));
        wallet.setCreatedAt(LocalDateTime.now());

        boutique.setWallet(wallet);
        boutiqueRepository.save(boutique);
        System.out.println("Initialized missing wallet for boutique: " + boutique.getName());
      }
    }
  }

  @Override
  @Transactional
  public BoutiqueResponse create(BoutiqueRequest request) {
    Boutique boutique = boutiqueMapper.toEntity(request);

    if (request.merchantTrackingId() != null) {
      Merchant merchant = merchantRepository
          .findByTrackingId(request.merchantTrackingId())
          .orElseThrow(
              () -> new EntityNotFoundException(
                  "Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
      boutique.setMerchant(merchant);
    }

    if (request.walletTrackingId() == null) {
      Wallet wallet = new Wallet();
      wallet.setTrackingId(UUID.randomUUID());
      wallet.setWalletType(WalletType.BOUTIQUE);
      wallet.setStatus(WalletStatus.INACTIF);
      wallet.setBalance(BigDecimal.ZERO);

      BigDecimal quotaBoutique = parametreGnsService.findByNomParametre(TypeParametreGns.MAJORATION_MONTANT_BOUTIQUE)
          .map(p -> new BigDecimal(p.valeurParametre()))
          .orElse(new BigDecimal("100000"));

      wallet.setLimitAmount(quotaBoutique);
      wallet.setCreatedAt(LocalDateTime.now());

      boutique.setWallet(wallet);
    } else {
      Wallet wallet = walletRepository
          .findByTrackingId(request.walletTrackingId())
          .orElseThrow(
              () -> new EntityNotFoundException(
                  "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      boutique.setWallet(wallet);
    }

    Boutique savedBoutique = boutiqueRepository.save(boutique);
    return boutiqueMapper.toResponse(savedBoutique);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<BoutiqueResponse> findByTrackingId(UUID trackingId) {
    return boutiqueRepository.findByTrackingId(trackingId).map(boutiqueMapper::toResponse);
  }

  @Override
  @Transactional
  public BoutiqueResponse update(UUID trackingId, BoutiqueRequest request) {
    Boutique boutique = boutiqueRepository
        .findByTrackingId(trackingId)
        .orElseThrow(
            () -> new EntityNotFoundException("Boutique non trouvée avec l'ID: " + trackingId));

    boutique.setName(request.name() != null ? request.name() : boutique.getName());
    boutique.setDescription(request.description() != null ? request.description() : boutique.getDescription());
    boutique.setKycStatus(request.kycStatus() != null ? request.kycStatus() : boutique.getKycStatus());
    boutique.setLatitude(request.latitude() != null ? request.latitude() : boutique.getLatitude());
    boutique.setLongitude(request.longitude() != null ? request.longitude() : boutique.getLongitude());
    boutique.setImageUrl(request.imageUrl() != null ? request.imageUrl() : boutique.getImageUrl());

    if (request.merchantTrackingId() != null) {
      Merchant merchant = merchantRepository
          .findByTrackingId(request.merchantTrackingId())
          .orElseThrow(
              () -> new EntityNotFoundException(
                  "Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
      boutique.setMerchant(merchant);
    }

    if (request.walletTrackingId() != null) {
      Wallet wallet = walletRepository
          .findByTrackingId(request.walletTrackingId())
          .orElseThrow(
              () -> new EntityNotFoundException(
                  "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      boutique.setWallet(wallet);
    }

    Boutique updatedBoutique = boutiqueRepository.save(boutique);
    return boutiqueMapper.toResponse(updatedBoutique);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Boutique boutique = boutiqueRepository
        .findByTrackingId(trackingId)
        .orElseThrow(
            () -> new EntityNotFoundException("Boutique non trouvée avec l'ID: " + trackingId));
    boutiqueRepository.delete(boutique);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BoutiqueResponse> findByMerchantTrackingId(
      UUID merchantTrackingId, Pageable pageable) {
    return boutiqueRepository
        .findByMerchantTrackingId(merchantTrackingId, normalize(pageable))
        .map(boutiqueMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<BoutiqueResponse> findByWalletTrackingId(UUID walletTrackingId) {
    return boutiqueRepository
        .findByWalletTrackingId(walletTrackingId)
        .map(boutiqueMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BoutiqueResponse> findByStatutKYC(KycStatus statutKYC, Pageable pageable) {
    return boutiqueRepository
        .findByKycStatus(statutKYC, normalize(pageable))
        .map(boutiqueMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BoutiqueResponse> findAll(Pageable pageable) {
    return boutiqueRepository.findAll(normalize(pageable)).map(boutiqueMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public long countLowQuota() {
    return boutiqueRepository.findAll().stream()
        .filter(b -> b.getWallet() != null && b.getWallet().getBalance() != null
            && b.getWallet().getLimitAmount() != null &&
            b.getWallet().getBalance().compareTo(b.getWallet().getLimitAmount().multiply(new BigDecimal("0.10"))) <= 0)
        .count();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BoutiqueResponse> getBoutiquesEnAlerteQuota(
      BigDecimal seuilPourcentage, Pageable pageable) {
    return boutiqueRepository
        .findBoutiquesEnAlerteQuota(seuilPourcentage, normalize(pageable))
        .map(boutiqueMapper::toResponse);
  }

  @Override
  @Transactional
  public BoutiqueResponse uploadBoutiqueImage(UUID trackingId, org.springframework.web.multipart.MultipartFile file) {
    Boutique boutique = boutiqueRepository.findByTrackingId(trackingId)
        .orElseThrow(() -> new EntityNotFoundException("Boutique non trouvée avec l'ID: " + trackingId));

    if (boutique.getImagePublicId() != null) {
      cloudinaryService.supprimer(boutique.getImagePublicId());
    }

    java.util.Map<String, String> uploadResult = cloudinaryService.upload(file, trackingId.toString());
    boutique.setImageUrl(uploadResult.get("url"));
    boutique.setImagePublicId(uploadResult.get("publicId"));

    return boutiqueMapper.toResponse(boutiqueRepository.save(boutique));
  }
}
