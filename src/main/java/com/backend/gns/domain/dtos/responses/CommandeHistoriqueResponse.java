package com.backend.gns.domain.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour l'historique des commandes d'un étudiant (C2)
 * Inclut les informations du commerçant pour afficher le nom de la boutique
 */
public record CommandeHistoriqueResponse(
        UUID trackingId,
        String reference,
        Double montantTotal,
        LocalDateTime dateCommande,
        String statut,
        String nomBoutique
) {}
