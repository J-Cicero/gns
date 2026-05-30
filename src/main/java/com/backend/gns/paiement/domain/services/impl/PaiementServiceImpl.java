package com.backend.gns.paiement.domain.services.impl;

import com.backend.gns.paiement.application.dtos.requests.PaiementRequest;
import com.backend.gns.paiement.application.dtos.responses.PaiementResponse;
import com.backend.gns.paiement.application.mappers.PaiementMapper;
import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import com.backend.gns.paiement.domain.models.Paiement;
import com.backend.gns.paiement.domain.services.PaiementService;
import com.backend.gns.paiement.infrastructure.repositories.PaiementRepository;
import com.backend.gns.paiement.infrastructure.repositories.CommandeRepository;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import com.backend.gns.paiement.domain.models.Commande;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.parametrage.domain.enums.TypeParametreGns;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaiementServiceImpl implements PaiementService {

  private static final int DEFAULT_PAGE_SIZE = 10;

  private final PaiementRepository paiementRepository;
  private final CommandeRepository commandeRepository;
  private final WalletRepository walletRepository;
  private final PaiementMapper paiementMapper;
  private final ParametreGnsService parametreGnsService;

  public PaiementServiceImpl(
      PaiementRepository paiementRepository,
      CommandeRepository commandeRepository,
      WalletRepository walletRepository,
      PaiementMapper paiementMapper,
      ParametreGnsService parametreGnsService) {
    this.paiementRepository = paiementRepository;
    this.commandeRepository = commandeRepository;
    this.walletRepository = walletRepository;
    this.paiementMapper = paiementMapper;
    this.parametreGnsService = parametreGnsService;
  }

  private Pageable normalize(Pageable pageable) {
    int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
    return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
  }

  @Override
  @Transactional
  public PaiementResponse create(PaiementRequest request) {
    Commande commande = null;
    if (request.commandeTrackingId() != null) {
      commande =
          commandeRepository
              .findByTrackingId(request.commandeTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Commande non trouvée avec l'ID: " + request.commandeTrackingId()));
    }

    Wallet wallet = null;
    if (request.walletTrackingId() != null) {
      wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
    }

    Paiement paiement = paiementMapper.toEntity(request, commande, wallet);
    Paiement savedPaiement = paiementRepository.save(paiement);
    return paiementMapper.toResponse(savedPaiement);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PaiementResponse> findByTrackingId(UUID trackingId) {
    return paiementRepository.findByTrackingId(trackingId).map(paiementMapper::toResponse);
  }

  @Override
  @Transactional
  public PaiementResponse update(UUID trackingId, PaiementRequest request) {
    Paiement paiement =
        paiementRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Paiement non trouvé avec l'ID: " + trackingId));

    paiement.setMontantDebite(request.montantDebite());
    // Recalculate commission and montantNetBoutique dynamically
    BigDecimal taux = parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.TAUX_COMMISSION_PAIEMENT);
    BigDecimal commission = request.montantDebite().multiply(taux);
    paiement.setCommission(commission);
    paiement.setMontantNetBoutique(request.montantDebite().subtract(commission));
    paiement.setDate(request.date());
    paiement.setTypePaiement(request.typePaiement());
    paiement.setStatutPaiement(request.statutPaiement());

    Paiement updatedPaiement = paiementRepository.save(paiement);
    return paiementMapper.toResponse(updatedPaiement);
  }

  @Override
  @Transactional
  public void delete(UUID trackingId) {
    Paiement paiement =
        paiementRepository
            .findByTrackingId(trackingId)
            .orElseThrow(
                () -> new EntityNotFoundException("Paiement non trouvé avec l'ID: " + trackingId));
    paiementRepository.delete(paiement);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByStatutPaiement(
      PaiementStatut statutPaiement, Pageable pageable) {
    return paiementRepository
        .findByStatutPaiementOrderByDateDesc(statutPaiement, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByPaiementStatut(
      PaiementStatut paiementStatut, Pageable pageable) {
    return paiementRepository
        .findByStatutPaiementOrderByDateDesc(paiementStatut, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByTypePaiement(PaiementType typePaiement, Pageable pageable) {
    return paiementRepository
        .findByTypePaiementOrderByDateDesc(typePaiement, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByPaiementType(PaiementType paiementType, Pageable pageable) {
    return paiementRepository
        .findByTypePaiementOrderByDateDesc(paiementType, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByCommandeTrackingId(
      UUID commandeTrackingId, Pageable pageable) {
    return paiementRepository
        .findByCommandeTrackingId(commandeTrackingId, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable) {
    return paiementRepository
        .findByWalletTrackingId(walletTrackingId, normalize(pageable))
        .map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findAll(Pageable pageable) {
    return paiementRepository.findAll(normalize(pageable)).map(paiementMapper::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaiementResponse> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable) {
    return paiementRepository.findByUniversiteTrackingId(universiteTrackingId, normalize(pageable))
        .map(paiementMapper::toResponse);
  }
}
