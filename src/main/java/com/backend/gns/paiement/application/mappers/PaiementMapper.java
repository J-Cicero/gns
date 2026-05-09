package com.backend.gns.paiement.application.mappers;

import com.backend.gns.paiement.application.dtos.requests.PaiementRequest;
import com.backend.gns.paiement.application.dtos.responses.PaiementResponse;
import com.backend.gns.paiement.domain.models.Commande;
import com.backend.gns.paiement.domain.models.Paiement;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.paiement.infrastructure.repositories.CommandeRepository;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaiementMapper {

  private final CommandeRepository commandeRepository;
  private final WalletRepository walletRepository;

  public PaiementMapper(CommandeRepository commandeRepository, WalletRepository walletRepository) {
    this.commandeRepository = commandeRepository;
    this.walletRepository = walletRepository;
  }

  public Paiement toEntity(PaiementRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête PaiementRequest ne peut pas être nulle");
    }

    Paiement paiement = new Paiement();
    paiement.setTrackingId(UUID.randomUUID());
    paiement.setMontantDebite(request.montantDebite());
    // Commission is calculated as 1% of montantDebite
    BigDecimal commission = request.montantDebite().multiply(new BigDecimal("0.01"));
    paiement.setCommission(commission);
    // montantNetBoutique is montantDebite - commission
    paiement.setMontantNetBoutique(request.montantDebite().subtract(commission));
    paiement.setDate(request.date());
    paiement.setTypePaiement(request.typePaiement());
    paiement.setStatutPaiement(request.statutPaiement());

    if (request.commandeTrackingId() != null) {
      Commande commande =
          commandeRepository
              .findByTrackingId(request.commandeTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Commande non trouvée avec l'ID: " + request.commandeTrackingId()));
      paiement.setCommande(commande);
    }

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + request.walletTrackingId()));
      paiement.setWallet(wallet);
    }

    return paiement;
  }

  public PaiementResponse toResponse(Paiement paiement) {
    if (paiement == null) {
      throw new IllegalArgumentException("L'entité Paiement ne peut pas être nulle");
    }

    return PaiementResponse.builder()
        .trackingId(paiement.getTrackingId())
        // .montantCommande(paiement.getCommande() != null ? paiement.getCommande().getMontantTotal() : null) // Removed
        .commission(paiement.getCommission())
        .montantDebite(paiement.getMontantDebite())
        .montantNetBoutique(paiement.getMontantNetBoutique()) // Added
        .date(paiement.getDate())
        .typePaiement(paiement.getTypePaiement())
        .statutPaiement(paiement.getStatutPaiement())
        .commandeTrackingId(
            paiement.getCommande() != null ? paiement.getCommande().getTrackingId() : null)
        .walletTrackingId(
            paiement.getWallet() != null ? paiement.getWallet().getTrackingId() : null)
        .build();
  }

  public Paiement toEntityFromResponse(PaiementResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse PaiementResponse ne peut pas être nulle");
    }

    Paiement paiement = new Paiement();
    paiement.setTrackingId(response.trackingId());
    // paiement.setCommission(response.commission()); // Removed as it's calculated
    paiement.setMontantDebite(response.montantDebite());
    paiement.setMontantNetBoutique(response.montantNetBoutique());
    paiement.setDate(response.date());
    paiement.setTypePaiement(response.typePaiement());
    paiement.setStatutPaiement(response.statutPaiement());

    if (response.commandeTrackingId() != null) {
      Commande commande =
          commandeRepository
              .findByTrackingId(response.commandeTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Commande non trouvée avec l'ID: " + response.commandeTrackingId()));
      paiement.setCommande(commande);
    }

    if (response.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(response.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + response.walletTrackingId()));
      paiement.setWallet(wallet);
    }

    return paiement;
  }
}
