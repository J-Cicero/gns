package com.backend.gns.admin.domain.services.impl;

import com.backend.gns.admin.application.dtos.requests.BankOperatorRequest;
import com.backend.gns.admin.application.dtos.responses.BankOperatorResponse;
import com.backend.gns.admin.application.mappers.BankOperatorMapper;
import com.backend.gns.Shared.exception.ResourceNotFoundException;
import com.backend.gns.Shared.user.domain.models.BankOperator;
import com.backend.gns.admin.domain.services.BankOperatorService;
import com.backend.gns.Shared.user.infrastructure.repositories.BankOperatorRepository;
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
public class BankOperatorServiceImpl implements BankOperatorService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final BankOperatorRepository bankOperatorRepository;
  private final BankOperatorMapper bankOperatorMapper;

  // ─────────────────────────────────────────────
  // Utilitaires privés
  // ─────────────────────────────────────────────

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private BankOperator findBankOperatorOrThrow(UUID trackingId) {
    return bankOperatorRepository
        .findByTrackingId(trackingId)
        .orElseThrow(() -> {
          log.warn("Opérateur bancaire introuvable avec trackingId: {}", trackingId);
          return new ResourceNotFoundException(
              "Opérateur bancaire non trouvé avec l'ID: " + trackingId);
        });
  }

  @Override
  @Transactional
  public BankOperatorResponse create(BankOperatorRequest request) {
    log.info("Création d'un opérateur bancaire: {} {}", request.prenom(), request.nom());

    BankOperator bankOperator = bankOperatorMapper.toEntity(request);
    BankOperator savedBankOperator = bankOperatorRepository.save(bankOperator);

    log.info("Opérateur bancaire créé avec succès, trackingId: {}", savedBankOperator.getTrackingId());
    return bankOperatorMapper.toResponse(savedBankOperator);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<BankOperatorResponse> findByTrackingId(UUID trackingId) {
    log.debug("Recherche opérateur bancaire par trackingId: {}", trackingId);
    return bankOperatorRepository.findByTrackingId(trackingId).map(bankOperatorMapper::toResponse);
  }

  @Override
  @Transactional
  public BankOperatorResponse update(UUID trackingId, BankOperatorRequest request) {
    log.info("Mise à jour opérateur bancaire trackingId: {}", trackingId);

    BankOperator bankOperator = findBankOperatorOrThrow(trackingId);

    bankOperator.setEmail(request.email());
    bankOperator.setNom(request.nom());
    bankOperator.setPrenom(request.prenom());
    bankOperator.setTelephone(request.telephone());

    BankOperator updatedBankOperator = bankOperatorRepository.save(bankOperator);
    log.info("Opérateur bancaire mis à jour avec succès, trackingId: {}", trackingId);
    return bankOperatorMapper.toResponse(updatedBankOperator);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    log.info("Suppression opérateur bancaire trackingId: {}", trackingId);
    BankOperator bankOperator = findBankOperatorOrThrow(trackingId);
    bankOperatorRepository.delete(bankOperator);
    log.info("Opérateur bancaire supprimé avec succès, trackingId: {}", trackingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<BankOperatorResponse> findAll(Pageable pageable) {
    log.debug("Récupération de tous les opérateurs bancaires, page: {}", pageable.getPageNumber());
    return bankOperatorRepository.findAll(normalize(pageable)).map(bankOperatorMapper::toResponse);
  }
}