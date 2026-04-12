package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.BudgetVirtuelRequest;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;
import com.backend.gns.domain.models.BudgetVirtuel;

import java.util.List;
import java.util.UUID;

public interface BudgetVirtuelService {

    BudgetVirtuelResponse create(BudgetVirtuelRequest request);

    List<BudgetVirtuelResponse> getAll();

    BudgetVirtuelResponse getByTrackingId(UUID trackingId);

    BudgetVirtuelResponse update(UUID trackingId, BudgetVirtuelRequest request);

    void delete(UUID trackingId);

    /**
     * F10 - Verifie et debite le budget virtuel du commercant pour le mois en cours.
     * Leve IllegalStateException si budget epuise ou insuffisant.
     */
    BudgetVirtuel verifierEtDebiterBudget(UUID merchantTrackingId, Double montant);

    /**
     * F9 - Alloue un budget virtuel mensuel pour un commercant.
     * Verifie qu'il n'existe pas deja un budget pour ce mois.
     */
    BudgetVirtuelResponse allocuerBudget(BudgetVirtuelRequest request);
}
