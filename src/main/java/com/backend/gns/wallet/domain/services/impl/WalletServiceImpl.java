package com.backend.gns.wallet.domain.services.impl;

import com.backend.gns.user.domain.exception.ResourceNotFoundException;
import com.backend.gns.wallet.application.dtos.requests.WalletRequest;
import com.backend.gns.wallet.application.dtos.responses.WalletResponse;
import com.backend.gns.wallet.application.mappers.WalletMapper;
import com.backend.gns.wallet.domain.enums.WalletStatus;
import com.backend.gns.wallet.domain.enums.WalletType;
import com.backend.gns.wallet.domain.enums.VersementStatut;
import com.backend.gns.wallet.domain.enums.VersementType;
import com.backend.gns.wallet.domain.models.Versement;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.domain.services.WalletService;
import com.backend.gns.wallet.infrastructure.repositories.VersementRepository;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import java.time.LocalDateTime;
import java.math.BigDecimal;
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
public class WalletServiceImpl implements WalletService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final WalletRepository walletRepository;
  private final WalletMapper walletMapper;
  private final VersementRepository versementRepository;

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  private Wallet findWalletOrThrow(UUID trackingId) {
    return walletRepository
        .findByTrackingId(trackingId)
        .orElseThrow(
            () -> {
              log.warn("Portefeuille introuvable avec trackingId: {}", trackingId);
              return new ResourceNotFoundException(
                  "Portefeuille non trouvé avec l'ID: " + trackingId);
            });
  }

  private void ensureWalletCanReceiveFunds(Wallet wallet) {
    if (wallet.getStatutWallet() == WalletStatus.GELE
        || wallet.getStatutWallet() == WalletStatus.BLOQUE) {
      throw new IllegalStateException(
          "Crédit refusé : le wallet est gelé ou bloqué (" + wallet.getStatutWallet() + ")");
    }
  }

  private void ensureWalletCanSpend(Wallet wallet) {
    if (wallet.getStatutWallet() != WalletStatus.ACTIF) {
      throw new IllegalStateException(
          "Débit refusé : le wallet doit être actif (statut actuel: "
              + wallet.getStatutWallet()
              + ")");
    }
  }

  @Override
  @Transactional
  public WalletResponse create(WalletRequest request) {
    log.info("Création d'un portefeuille de type: {}", request.typeWallet());

    Wallet wallet = walletMapper.toEntity(request);
    Wallet savedWallet = walletRepository.save(wallet);

    log.info("Portefeuille créé avec succès, trackingId: {}", savedWallet.getTrackingId());
    return walletMapper.toResponse(savedWallet);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<WalletResponse> findByTrackingId(UUID trackingId) {
    log.debug("Recherche portefeuille par trackingId: {}", trackingId);
    return walletRepository.findByTrackingId(trackingId).map(walletMapper::toResponse);
  }

  @Override
  @Transactional
  public WalletResponse update(UUID trackingId, WalletRequest request) {
    log.info("Mise à jour portefeuille trackingId: {}", trackingId);

    Wallet wallet = findWalletOrThrow(trackingId);

    wallet.setTypeWallet(request.typeWallet());
    wallet.setStatutWallet(request.statutWallet());
    wallet.setSolde(request.solde());
    wallet.setPlafond(request.plafond());

    Wallet updated = walletRepository.save(wallet);
    log.info("Portefeuille mis à jour avec succès, trackingId: {}", trackingId);
    return walletMapper.toResponse(updated);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    log.info("Suppression portefeuille trackingId: {}", trackingId);
    Wallet wallet = findWalletOrThrow(trackingId);
    walletRepository.delete(wallet);
    log.info("Portefeuille supprimé avec succès, trackingId: {}", trackingId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WalletResponse> findByTypeWallet(WalletType typeWallet, Pageable pageable) {
    log.debug("Recherche portefeuilles par type: {}", typeWallet);
    return walletRepository
        .findByTypeWallet(typeWallet, normalize(pageable))
        .map(walletMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WalletResponse> findFiltered(WalletType typeWallet, com.backend.gns.wallet.domain.enums.WalletFundingLevel niveauSolde, Pageable pageable) {
    log.debug("Recherche portefeuilles filtres: type={}, niveau={}", typeWallet, niveauSolde);
    return walletRepository
        .findFiltered(typeWallet, niveauSolde, normalize(pageable))
        .map(walletMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WalletResponse> findByStatutWallet(WalletStatus statutWallet, Pageable pageable) {
    log.debug("Recherche portefeuilles par statut: {}", statutWallet);
    return walletRepository
        .findByStatutWallet(statutWallet, normalize(pageable))
        .map(walletMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WalletResponse> findByNiveauSolde(com.backend.gns.wallet.domain.enums.WalletFundingLevel niveauSolde, Pageable pageable) {
    log.debug("Recherche portefeuilles par niveau: {}", niveauSolde);
    return walletRepository
        .findByNiveauSolde(niveauSolde, normalize(pageable))
        .map(walletMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WalletResponse> findBySoldeLessThan(BigDecimal amount, Pageable pageable) {
    log.debug("Recherche portefeuilles avec solde < {}", amount);
    return walletRepository
        .findBySoldeLessThan(amount, normalize(pageable))
        .map(walletMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WalletResponse> findBySoldeGreaterThan(BigDecimal amount, Pageable pageable) {
    log.debug("Recherche portefeuilles avec solde > {}", amount);
    return walletRepository
        .findBySoldeGreaterThan(amount, normalize(pageable))
        .map(walletMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WalletResponse> findAll(Pageable pageable) {
    log.debug("Récupération de tous les portefeuilles, page: {}", pageable.getPageNumber());
    return walletRepository.findAll(normalize(pageable)).map(walletMapper::toResponse);
  }

  @Override
  @Transactional
  public void crediter(UUID walletTrackingId, BigDecimal montant) {
    log.info("Crédit portefeuille trackingId: {}, montant: {}", walletTrackingId, montant);

    if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Le montant à créditer doit être strictement positif.");
    }

    Wallet wallet = findWalletOrThrow(walletTrackingId);
    ensureWalletCanReceiveFunds(wallet);

    BigDecimal nouveauSolde = wallet.getSolde().add(montant);

    if (nouveauSolde.compareTo(wallet.getPlafond()) > 0) {
      throw new IllegalStateException(
          "Crédit refusé : le solde dépasserait le plafond autorisé de " + wallet.getPlafond());
    }

    wallet.setSolde(nouveauSolde);
    walletRepository.save(wallet);
    log.info("Crédit effectué. Nouveau solde: {}, trackingId: {}", nouveauSolde, walletTrackingId);
  }

  @Override
  @Transactional
  public void debiter(UUID walletTrackingId, BigDecimal montant) {
    log.info("Débit portefeuille trackingId: {}, montant: {}", walletTrackingId, montant);

    if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Le montant à débiter doit être strictement positif.");
    }

    Wallet wallet = findWalletOrThrow(walletTrackingId);
    ensureWalletCanSpend(wallet);
    if (wallet.getSolde().compareTo(montant) < 0) {
      throw new IllegalStateException(
          "Solde insuffisant. Solde actuel: "
              + wallet.getSolde()
              + ", Montant demandé: "
              + montant);
    }

    BigDecimal nouveauSolde = wallet.getSolde().subtract(montant);
    wallet.setSolde(nouveauSolde);
    walletRepository.save(wallet);
    log.info("Débit effectué. Nouveau solde: {}, trackingId: {}", nouveauSolde, walletTrackingId);
  }

  @Override
  @Transactional
  public void remettreAZero(UUID walletTrackingId) {
    log.info("Remise à zéro du portefeuille trackingId: {}", walletTrackingId);
    Wallet wallet = findWalletOrThrow(walletTrackingId);
    
    BigDecimal soldeActuel = wallet.getSolde();
    if (soldeActuel.compareTo(BigDecimal.ZERO) == 0) {
      log.info("Le portefeuille est déjà à 0.");
      return;
    }

    // Remise à zéro
    wallet.setSolde(BigDecimal.ZERO);
    walletRepository.save(wallet);

    // Traçabilité de l'opération
    Versement trace = new Versement();
    trace.setTrackingId(UUID.randomUUID());
    trace.setWallet(wallet);
    trace.setMontantVerse(soldeActuel.negate()); // On enregistre un montant négatif correspondant à la soustraction
    trace.setDateVersement(LocalDateTime.now());
    trace.setTypeVersement(VersementType.REMISE_A_ZERO);
    trace.setStatut(VersementStatut.VALIDEE);
    versementRepository.save(trace);

    log.info("Portefeuille réinitialisé avec succès.");
  }

  @Override
  @Transactional
  public void remettreAZeroGroupe(java.util.List<UUID> walletTrackingIds) {
    log.info("Remise à zéro en masse de {} portefeuilles", walletTrackingIds.size());
    for (UUID id : walletTrackingIds) {
        try {
            remettreAZero(id);
        } catch (Exception e) {
            log.error("Échec de la remise à zéro pour le portefeuille {}: {}", id, e.getMessage());
        }
    }
  }
}
