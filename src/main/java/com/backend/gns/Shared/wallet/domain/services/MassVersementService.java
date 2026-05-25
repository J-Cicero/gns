package com.backend.gns.Shared.wallet.domain.services;

import java.math.BigDecimal;
import java.util.UUID;

public interface MassVersementService {
    /**
     * Effectue un versement en masse pour tous les étudiants éligibles.
     * @param scolariteYearTrackingId L'année scolaire concernée.
     * @param montantFixe Optional: si présent, on utilise ce montant au lieu du montant calculé par l'éligibilité.
     */
    void versementMasseEtudiants(UUID scolariteYearTrackingId, BigDecimal montantFixe);

    /**
     * Effectue un versement à toutes les boutiques dont le solde est inférieur au seuil.
     * @param seuil Le solde minimum déclenchant le versement.
     * @param montantAVerser Le montant du quota à accorder.
     */
    void versementMasseBoutiques(BigDecimal seuil, BigDecimal montantAVerser);
}
