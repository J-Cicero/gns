package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.MerchantRequest;
import com.backend.gns.domain.dtos.responses.MerchantResponse;
import com.backend.gns.domain.dtos.responses.BudgetVirtuelResponse;
import com.backend.gns.domain.dtos.responses.CommandeHistoriqueResponse;

import java.util.List;
import java.util.UUID;

public interface MerchantService {

    MerchantResponse create(MerchantRequest request);

    List<MerchantResponse> getAll();

    MerchantResponse getByTrackingId(UUID trackingId);

    MerchantResponse update(UUID trackingId, MerchantRequest request);

    void delete(UUID trackingId);

    /**
     * C4 - Récupère le budget actif du mois courant d'un commerçant
     */
    BudgetVirtuelResponse getBudgetActif(UUID merchantTrackingId);

    /**
     * C5 - Récupère l'historique des commandes reçues par un commerçant
     */
    List<CommandeHistoriqueResponse> getCommandesRecues(UUID merchantTrackingId);
}
