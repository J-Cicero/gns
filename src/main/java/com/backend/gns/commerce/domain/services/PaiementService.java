package com.backend.gns.commerce.domain.services;

import com.backend.gns.commerce.domain.enums.PaiementStatut;
import com.backend.gns.commerce.domain.enums.PaiementType;
import com.backend.gns.commerce.application.dtos.requests.PaiementRequest;
import com.backend.gns.commerce.application.dtos.requests.QrPaymentRequest;
import com.backend.gns.commerce.application.dtos.responses.PaiementResponse;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaiementService {

  void traiterLiquidations();

  PaiementResponse processQrPayment(QrPaymentRequest request);

  PaiementResponse create(PaiementRequest request);

  Optional<PaiementResponse> findByTrackingId(UUID trackingId);

  PaiementResponse update(UUID trackingId, PaiementRequest request);

  void delete(UUID trackingId);

  Page<PaiementResponse> findByStatutPaiement(PaiementStatut statutPaiement, Pageable pageable);

  Page<PaiementResponse> findByPaiementStatut(PaiementStatut paiementStatut, Pageable pageable);

  Page<PaiementResponse> findByTypePaiement(PaiementType typePaiement, Pageable pageable);

  Page<PaiementResponse> findByPaiementType(PaiementType paiementType, Pageable pageable);

  Page<PaiementResponse> findByCommandeTrackingId(UUID commandeTrackingId, Pageable pageable);

  Page<PaiementResponse> findByWalletTrackingId(UUID walletTrackingId, Pageable pageable);

  Page<PaiementResponse> findAll(Pageable pageable);

  Page<PaiementResponse> findByUniversiteTrackingId(UUID universiteTrackingId, Pageable pageable);

  java.util.Map<String, Object> getStats();
}
