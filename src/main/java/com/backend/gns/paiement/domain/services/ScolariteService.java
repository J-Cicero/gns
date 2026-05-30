package com.backend.gns.paiement.domain.services;

import com.backend.gns.paiement.application.dtos.responses.PretScolariteResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ScolariteService {
  PretScolariteResponse demanderPretScolarite(
      UUID studentTrackingId, UUID universiteTrackingId, BigDecimal montant);

  void rembourserPretsEnAttente(UUID studentTrackingId, BigDecimal montantDisponible);

  List<PretScolariteResponse> findByUniversite(UUID universiteTrackingId);
}
