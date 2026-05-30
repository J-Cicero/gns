package com.backend.gns.admin.domain.services.impl;

import com.backend.gns.admin.application.dtos.requests.BankOperatorRequest;
import com.backend.gns.admin.application.dtos.responses.BankOperatorResponse;
import com.backend.gns.admin.application.mappers.BankOperatorMapper;
import com.backend.gns.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.core.exception.ResourceNotFoundException;
import com.backend.gns.user.domain.models.BankOperator;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.admin.domain.services.BankOperatorService;
import com.backend.gns.user.infrastructure.repositories.BankOperatorRepository;
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
public class BankOperatorServiceImpl implements BankOperatorService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final BankOperatorRepository bankOperatorRepository;
  private final BankOperatorMapper bankOperatorMapper;
  private final ParametreGnsService parametreGnsService;

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
    
    // Initialisation du Wallet via Cascade
    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setTypeWallet(WalletType.BANK_OPERATOR);
    wallet.setStatutWallet(WalletStatus.ACTIF);
    wallet.setSolde(BigDecimal.ZERO);
    
    BigDecimal plafondDefaut = parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.MONTANT_DEFAUT_WALLET);
    wallet.setPlafond(plafondDefaut);
    wallet.setDateCreation(LocalDateTime.now());
    
    bankOperator.setWallet(wallet);

    BankOperator savedBankOperator = bankOperatorRepository.save(bankOperator);

    log.info("Opérateur bancaire créé avec succès avec son Wallet, trackingId: {}", savedBankOperator.getTrackingId());
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