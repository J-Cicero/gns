package com.backend.gns.domain.services;

import com.backend.gns.domain.dtos.requests.WalletRequest;
import com.backend.gns.domain.dtos.responses.WalletResponse;
import com.backend.gns.domain.dtos.responses.PaiementResponse;

import java.util.List;
import java.util.UUID;

public interface WalletService {

    WalletResponse create(WalletRequest request);

    List<WalletResponse> getAll();

    WalletResponse getByTrackingId(UUID trackingId);

    WalletResponse update(UUID trackingId, WalletRequest request);

    void delete(UUID trackingId);

    /**
     * Verrouille un wallet par son trackingId (estVerouille = true).
     */
    WalletResponse verrouillerWallet(UUID walletTrackingId);

    /**
     * F6 - Deverrouille un wallet (estVerouille = false).
     */
    WalletResponse deverrouillerWallet(UUID walletTrackingId);

    /**
     * F2 - Cree un versement BOURSE_DBS et credite 14/15 du plafond sur le wallet HORIZON.
     */
    WalletResponse crediterHorizon(UUID walletTrackingId);

    /**
     * C3 - Récupère l'historique des paiements d'un wallet
     */
    List<PaiementResponse> getPaiementsOfWallet(UUID walletTrackingId);
}
