package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.MerchantRequest;
import com.backend.gns.commerce.application.dtos.responses.MerchantResponse;
import com.backend.gns.commerce.application.mappers.MerchantMapper;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.services.MerchantService;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.domain.enums.KycStatus;
import com.backend.gns.core.parametrage.domain.models.CompteBancaire;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.core.parametrage.infrastructure.repositories.BanqueRepository;
import com.backend.gns.core.parametrage.infrastructure.repositories.CompteBancaireRepository;
import com.backend.gns.user.infrastructure.repositories.UserRepository;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final MerchantRepository merchantRepository;
  private final MerchantMapper merchantMapper;
  private final UserRepository userRepository;
  private final BoutiqueRepository boutiqueRepository;
  private final BanqueRepository banqueRepository;
  private final CompteBancaireRepository compteBancaireRepository;
  private final CloudinaryStorageService storageService;
  private final PasswordEncoder passwordEncoder;

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private Merchant findMerchantOrThrow(UUID trackingId) {
    return merchantRepository
        .findByTrackingId(trackingId)
        .orElseThrow(
            () -> {
              log.warn("Marchand introuvable avec trackingId: {}", trackingId);
              return new ResourceNotFoundException("Marchand non trouvé avec l'ID: " + trackingId);
            });
  }

  @Override
  @Transactional
  public MerchantResponse create(MerchantRequest request, org.springframework.web.multipart.MultipartFile rib) {
    log.info("Inscription commerçant complète pour: {}", request.email());

    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("Cet email est déjà utilisé par un autre compte.");
    }

    Merchant merchant = merchantMapper.toEntity(request);
    merchant.setTrackingId(UUID.randomUUID());
    merchant.setRole(com.backend.gns.user.domain.enums.UserRole.COMMERCANT);
    merchant.setActive(true);
    merchant.setPasswordHash(passwordEncoder.encode(request.password()));
    
    Merchant savedMerchantUser = merchantRepository.save(merchant);

    com.backend.gns.commerce.domain.models.Boutique boutique = new com.backend.gns.commerce.domain.models.Boutique();
    boutique.setTrackingId(UUID.randomUUID());
    boutique.setName(request.businessName());
    boutique.setMerchant(savedMerchantUser);
    boutique.setKycStatus(KycStatus.EN_ATTENTE);

    com.backend.gns.wallet.domain.models.Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setWalletType(WalletType.BOUTIQUE);
    wallet.setStatus(WalletStatus.ACTIF);
    wallet.setBalance(BigDecimal.ZERO);
    wallet.setLimitAmount(BigDecimal.ZERO);
    wallet.setCreatedAt(java.time.LocalDateTime.now());
    boutique.setWallet(wallet);

    boutiqueRepository.save(boutique);

    if (rib != null) {
      try {
        var ribUpload = storageService.upload(rib, "rib_merchant_" + savedMerchantUser.getTrackingId());

        CompteBancaire cb = new CompteBancaire();
        cb.setTrackingId(UUID.randomUUID());
        cb.setOwnerTrackingId(savedMerchantUser.getTrackingId());
        cb.setOwnerType(com.backend.gns.core.parametrage.domain.enums.ProprietaireType.MERCHANT);
        cb.setAccountNumber(request.accountNumber());

        if (request.bankTrackingId() != null) {
          cb.setBank(banqueRepository.findByTrackingId(request.bankTrackingId()).orElse(null));
        }
        compteBancaireRepository.save(cb);
      } catch (Exception e) {
        throw new RuntimeException("Erreur lors de l'upload du RIB commerçant", e);
      }
    }

    log.info("Marchand créé avec succès, trackingId: {}", savedMerchantUser.getTrackingId());
    return merchantMapper.toResponse(savedMerchantUser);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<MerchantResponse> findByTrackingId(UUID trackingId) {
    log.debug("Recherche marchand par trackingId: {}", trackingId);
    return merchantRepository.findByTrackingId(trackingId).map(merchantMapper::toResponse);
  }

  @Override
  @Transactional
  public MerchantResponse update(UUID trackingId, MerchantRequest request) {
    log.info("Mise à jour marchand trackingId: {}", trackingId);

    Merchant merchant = findMerchantOrThrow(trackingId);

    merchant.setEmail(request.email());
    merchant.setLastName(request.lastName());
    merchant.setFirstName(request.firstName());
    merchant.setPhoneNumber(request.phoneNumber());
    merchant.setBirthDate(request.birthDate());

    Merchant updatedMerchant = merchantRepository.save(merchant);
    log.info("Marchand mis à jour avec succès, trackingId: {}", trackingId);
    return merchantMapper.toResponse(updatedMerchant);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    log.info("Suppression marchand trackingId: {}", trackingId);
    Merchant merchant = findMerchantOrThrow(trackingId);
    merchantRepository.delete(merchant);
    log.info("Marchand supprimé avec succès, trackingId: {}", trackingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<MerchantResponse> findAll(Pageable pageable) {
    log.debug("Récupération de tous les marchands, page: {}", pageable.getPageNumber());
    return merchantRepository.findAll(normalize(pageable)).map(merchantMapper::toResponse);
  }
}
