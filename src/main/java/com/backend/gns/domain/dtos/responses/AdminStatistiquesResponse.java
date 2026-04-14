package com.backend.gns.domain.dtos.responses;

/**
 * DTO pour les statistiques d'administration (C7)
 */
public record AdminStatistiquesResponse(
        Long totalEtudiants,
        Long totalCommerçants,
        Long totalTransactions,
        Double volumeTotal,
        Double commissionsCollectees,
        Long versementsEnAttente,
        Long dossiersKycEnAttente
) {}
