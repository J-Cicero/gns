package com.backend.gns.domain.services.impl;

import com.backend.gns.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.application.dtos.requests.WalletRequest;
import com.backend.gns.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.application.mappers.BoutiqueMapper;
import com.backend.gns.domain.enums.KycStatus;
import com.backend.gns.domain.enums.WalletStatus;
import com.backend.gns.domain.enums.WalletType;
import com.backend.gns.domain.models.Boutique;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.domain.models.Wallet;
import com.backend.gns.domain.services.BoutiqueService;
import com.backend.gns.domain.services.WalletService;
import com.backend.gns.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.infrastructure.repositories.WalletRepository;
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
public class BoutiqueServiceImpl implements BoutiqueService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final BoutiqueRepository boutiqueRepository;
  private final BoutiqueMapper boutiqueMapper;
  private final MerchantRepository merchantRepository;
  private final WalletRepository walletRepository;
  private final WalletService walletService;

  public BoutiqueServiceImpl(
      BoutiqueRepository boutiqueRepository,
      BoutiqueMapper boutiqueMapper,
      MerchantRepository merchantRepository,
      WalletRepository walletRepository,
      WalletService walletService) {
    this.boutiqueRepository = boutiqueRepository;
    this.boutiqueMapper = boutiqueMapper;
    this.merchantRepository = merchantRepository;
    this.walletRepository = walletRepository;
    this.walletService = walletService;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public BoutiqueResponse create(BoutiqueRequest request) {
    Boutique boutique = boutiqueMapper.toEntity(request);

    if (request.merchantTrackingId() != null) {
      Merchant merchant =
          merchantRepository
              .findByTrackingId(request.merchantTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
      boutique.setMerchant(merchant);
    }

    // Créer un Wallet pour la boutique avec type BOUTIQUE si pas fourni
    if (request.walletTrackingId() == null) {
      WalletRequest walletRequest =
          WalletRequest.builder()
              .typeWallet(WalletType.BOUTIQUE)
              .statutWallet(WalletStatus.ACTIF)
              .solde(BigDecimal.ZERO)
              .plafond(BigDecimal.ZERO)
              .estVerrouille(false)
              .dateCreation(LocalDateTime.now())
              .build();
      walletService.create(walletRequest);

      // Récupérer le dernier wallet créé et l'associer à la boutique
      var wallets = walletRepository.findByTypeWallet(WalletType.BOUTIQUE, Pageable.unpaged());
      if (wallets.hasContent()) {
        Wallet wallet = wallets.getContent().get(wallets.getContent().size() - 1);
        boutique.setWallet(wallet);
      }
    } else {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
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
    Boutique boutique =
        boutiqueRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Boutique non trouvée avec l'ID: " + trackingId));

    boutique.setNomBoutique(request.nomBoutique());
    boutique.setCategorieShop(request.categorieShop());
    boutique.setStatutKYC(request.statutKYC());
    boutique.setLatitude(request.latitude());
    boutique.setLongitude(request.longitude());

    if (request.merchantTrackingId() != null) {
      Merchant merchant =
          merchantRepository
              .findByTrackingId(request.merchantTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
      boutique.setMerchant(merchant);
    }

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      boutique.setWallet(wallet);
    }

    Boutique updatedBoutique = boutiqueRepository.save(boutique);
    return boutiqueMapper.toResponse(updatedBoutique);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Boutique boutique =
        boutiqueRepository
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
        .findByStatutKYC(statutKYC, normalize(pageable))
        .map(boutiqueMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BoutiqueResponse> findAll(Pageable pageable) {
    return boutiqueRepository.findAll(normalize(pageable)).map(boutiqueMapper::toResponse);
  }
}
