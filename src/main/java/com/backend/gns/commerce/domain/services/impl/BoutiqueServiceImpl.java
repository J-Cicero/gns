package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.BoutiqueRequest;
import com.backend.gns.commerce.application.dtos.responses.BoutiqueResponse;
import com.backend.gns.commerce.application.mappers.BoutiqueMapper;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.services.BoutiqueService;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.domain.enums.KycStatus;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.domain.services.WalletService;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    // Créer un Wallet pour la boutique si pas fourni via Cascade
    if (request.walletTrackingId() == null) {
      Wallet wallet = new Wallet();
      wallet.setTrackingId(UUID.randomUUID());
      wallet.setTypeWallet(WalletType.BOUTIQUE);
      wallet.setStatutWallet(WalletStatus.ACTIF);
      wallet.setSolde(BigDecimal.ZERO);
      wallet.setPlafond(BigDecimal.ZERO);
      wallet.setDateCreation(LocalDateTime.now());

      boutique.setWallet(wallet);
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

  @Override
  @Transactional(readOnly = true)
  public Page<BoutiqueResponse> getBoutiquesEnAlerteQuota(
      BigDecimal seuilPourcentage, Pageable pageable) {
    return boutiqueRepository
        .findAll(normalize(pageable))
        .map(
            b -> {
              if (b.getWallet() != null) {
                BigDecimal plafond = b.getWallet().getPlafond();
                BigDecimal solde = b.getWallet().getSolde();
                if (plafond.compareTo(BigDecimal.ZERO) > 0) {
                  BigDecimal ratio = solde.divide(plafond, 2, RoundingMode.HALF_UP);
                  if (ratio.compareTo(seuilPourcentage) <= 0) {
                    return boutiqueMapper.toResponse(b);
                  }
                }
              }
              return null;
            });
  }
}
