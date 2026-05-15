package com.backend.gns.paiement.domain.services.impl;

import com.backend.gns.paiement.application.dtos.requests.PaiementRequest;
import com.backend.gns.paiement.application.dtos.responses.PaiementResponse;
import com.backend.gns.paiement.application.mappers.PaiementMapper;
import com.backend.gns.paiement.domain.enums.PaiementStatut;
import com.backend.gns.paiement.domain.enums.PaiementType;
import com.backend.gns.paiement.domain.models.Paiement;
import com.backend.gns.paiement.domain.services.PaiementService;
import com.backend.gns.paiement.infrastructure.repositories.PaiementRepository;
import com.backend.gns.Shared.domain.services.ParametreGnsService;
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
  private final PaiementMapper paiementMapper;
  private final ParametreGnsService parametreGnsService;

  public PaiementServiceImpl(PaiementRepository paiementRepository, PaiementMapper paiementMapper, ParametreGnsService parametreGnsService) {
    this.paiementRepository = paiementRepository;
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
    Paiement paiement = paiementMapper.toEntity(request);
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
    BigDecimal taux = parametreGnsService.getValeurAsBigDecimal("TAUX_COMMISSION_PAIEMENT");
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
}
