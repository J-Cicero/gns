package com.backend.gns.commerce.domain.services.impl;

import com.backend.gns.commerce.application.dtos.requests.MerchantRequest;
import com.backend.gns.commerce.application.dtos.responses.MerchantResponse;
import com.backend.gns.commerce.application.mappers.MerchantMapper;
import com.backend.gns.commerce.domain.models.Merchant;
import com.backend.gns.commerce.domain.services.MerchantService;
import com.backend.gns.commerce.infrastructure.repositories.MerchantRepository;
import com.backend.gns.core.exception.ResourceNotFoundException;
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
public class MerchantServiceImpl implements MerchantService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final MerchantRepository merchantRepository;
  private final MerchantMapper merchantMapper;

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
  public MerchantResponse create(MerchantRequest request) {
    log.info("Création d'un marchand: {} {}", request.firstName(), request.lastName());

    Merchant merchant = merchantMapper.toEntity(request);
    Merchant savedMerchant = merchantRepository.save(merchant);

    log.info("Marchand créé avec succès, trackingId: {}", savedMerchant.getTrackingId());
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
