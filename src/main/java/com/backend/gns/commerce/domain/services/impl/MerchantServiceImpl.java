package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.MerchantRequest;
import com.backend.gns.commerce.application.dtos.responses.MerchantResponse;
import com.backend.gns.commerce.application.mappers.MerchantMapper;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.services.MerchantService;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.core.parametrage.domain.services.impl.CloudinaryStorageService;
import com.backend.gns.core.parametrage.infrastructure.repositories.BanqueRepository;
import com.backend.gns.core.parametrage.infrastructure.repositories.CompteBancaireRepository;
import com.backend.gns.user.infrastructure.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public MerchantResponse create(MerchantRequest request, org.springframework.web.multipart.MultipartFile ribFile) {
    log.info("Création de l'identité du commerçant: {}", request.email());

    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new IllegalArgumentException("Cet email est déjà utilisé.");
    }

    Merchant merchant = merchantMapper.toEntity(request);
    merchant.setTrackingId(UUID.randomUUID());
    merchant.setRole(com.backend.gns.user.domain.enums.UserRole.COMMERCANT);
    merchant.setPassword(passwordEncoder.encode(request.password()));

    Merchant savedMerchant = merchantRepository.save(merchant);

    if (request.businessName() != null && !request.businessName().isEmpty()) {
        com.backend.gns.commerce.domain.models.Boutique boutique = new com.backend.gns.commerce.domain.models.Boutique();
        boutique.setTrackingId(UUID.randomUUID());
        boutique.setName(request.businessName());
        boutique.setRegistrationNumber(request.registrationNumber());
        boutique.setMerchant(savedMerchant);
        boutique.setStatus(com.backend.gns.commerce.domain.enums.BoutiqueStatus.EN_ATTENTE);
        boutiqueRepository.save(boutique);
        log.info("Boutique créée avec succès pour le marchand");
    }

    if (request.bankTrackingId() != null && request.accountNumber() != null) {
        var banque = banqueRepository.findByTrackingId(request.bankTrackingId())
            .orElseThrow(() -> new IllegalArgumentException("Banque introuvable"));
            
        com.backend.gns.core.parametrage.domain.models.CompteBancaire compte = new com.backend.gns.core.parametrage.domain.models.CompteBancaire();
        compte.setTrackingId(UUID.randomUUID());
        compte.setAccountNumber(request.accountNumber());
        compte.setBanque(banque);
        compte.setOwnerId(savedMerchant.getTrackingId());
        compte.setOwnerType("MERCHANT");
        
        if (ribFile != null && !ribFile.isEmpty()) {
            try {
                String ribUrl = storageService.uploadFile(ribFile, "merchants/rib");
                compte.setRibUrl(ribUrl);
            } catch (Exception e) {
                log.error("Erreur lors de l'upload du RIB", e);
            }
        }
        
        compteBancaireRepository.save(compte);
        log.info("Compte bancaire créé pour le marchand");
    }

    log.info("Identité commerçant créée avec succès, trackingId: {}", savedMerchant.getTrackingId());
    return merchantMapper.toResponse(savedMerchant);
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
