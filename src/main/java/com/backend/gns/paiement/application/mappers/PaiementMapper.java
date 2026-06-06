package com.backend.gns.paiement.application.mappers;

import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.core.parametrage.domain.enums.TypeParametreGns;
import com.backend.gns.core.parametrage.domain.services.ParametreGnsService;
import com.backend.gns.paiement.application.dtos.requests.PaiementRequest;
import com.backend.gns.paiement.application.dtos.responses.PaiementResponse;
import com.backend.gns.paiement.domain.models.Commande;
import com.backend.gns.paiement.domain.models.Paiement;
import com.backend.gns.student.infrastructure.repositories.UniversiteRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PaiementMapper {

  private final ParametreGnsService parametreGnsService;
  private final BoutiqueRepository boutiqueRepository;
  private final UniversiteRepository universiteRepository;

  public PaiementMapper(
      ParametreGnsService parametreGnsService,
      BoutiqueRepository boutiqueRepository,
      UniversiteRepository universiteRepository) {
    this.parametreGnsService = parametreGnsService;
    this.boutiqueRepository = boutiqueRepository;
    this.universiteRepository = universiteRepository;
  }

  public Paiement toEntity(PaiementRequest request, Commande commande, Wallet wallet) {
    if (request == null) {
      throw new IllegalArgumentException("La requête PaiementRequest ne peut pas être nulle");
    }

    Paiement paiement = new Paiement();
    paiement.setTrackingId(UUID.randomUUID());
    paiement.setMontantDebite(request.montantDebite());
    // Commission is calculated dynamically
    BigDecimal taux =
        parametreGnsService.getValeurAsBigDecimal(TypeParametreGns.TAUX_COMMISSION_PAIEMENT);
    BigDecimal commission = request.montantDebite().multiply(taux);
    paiement.setCommission(commission);
    // montantNetBoutique is montantDebite - commission
    paiement.setMontantNetBoutique(request.montantDebite().subtract(commission));
    paiement.setDate(request.date());
    paiement.setTypePaiement(request.typePaiement());
    paiement.setStatutPaiement(request.statutPaiement());

    paiement.setCommande(commande);
    paiement.setWallet(wallet);

    return paiement;
  }

  public PaiementResponse toResponse(Paiement paiement) {
    if (paiement == null) {
      throw new IllegalArgumentException("L'entité Paiement ne peut pas être nulle");
    }

    String senderName = null;
    if (paiement.getStudent() != null) {
      senderName = paiement.getStudent().getNom() + " " + paiement.getStudent().getPrenom();
    }

    String receiverName = null;
    String receiverType = null;
    if (paiement.getWallet() != null) {
      if (paiement.getWallet().getTypeWallet()
          == com.backend.gns.wallet.domain.enums.WalletType.BOUTIQUE) {
        boutiqueRepository
            .findByWalletTrackingId(paiement.getWallet().getTrackingId())
            .ifPresent(
                boutique -> {
                  // Hacky way to set effectively final variables in lambda is not needed if we do:
                });
        com.backend.gns.commerce.domain.models.Boutique boutique =
            boutiqueRepository
                .findByWalletTrackingId(paiement.getWallet().getTrackingId())
                .orElse(null);
        if (boutique != null) {
          receiverName = boutique.getNomBoutique();
          receiverType = "Boutique";
        }
      } else if (paiement.getWallet().getTypeWallet()
          == com.backend.gns.wallet.domain.enums.WalletType.UNIVERSITY) {
        com.backend.gns.student.domain.models.Universite universite =
            universiteRepository
                .findByWalletTrackingId(paiement.getWallet().getTrackingId())
                .orElse(null);
        if (universite != null) {
          receiverName = universite.getNom();
          receiverType = "Université";
        }
      }
    }

    return PaiementResponse.builder()
        .trackingId(paiement.getTrackingId())
        .commission(paiement.getCommission())
        .montantDebite(paiement.getMontantDebite())
        .montantNetBoutique(paiement.getMontantNetBoutique())
        .date(paiement.getDate())
        .typePaiement(paiement.getTypePaiement())
        .statutPaiement(paiement.getStatutPaiement())
        .commandeTrackingId(
            paiement.getCommande() != null ? paiement.getCommande().getTrackingId() : null)
        .walletTrackingId(
            paiement.getWallet() != null ? paiement.getWallet().getTrackingId() : null)
        .senderName(senderName)
        .receiverName(receiverName)
        .receiverType(receiverType)
        .build();
  }

  public Paiement toEntityFromResponse(
      PaiementResponse response, Commande commande, Wallet wallet) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse PaiementResponse ne peut pas être nulle");
    }

    Paiement paiement = new Paiement();
    paiement.setTrackingId(response.trackingId());
    paiement.setMontantDebite(response.montantDebite());
    paiement.setMontantNetBoutique(response.montantNetBoutique());
    paiement.setDate(response.date());
    paiement.setTypePaiement(response.typePaiement());
    paiement.setStatutPaiement(response.statutPaiement());

    paiement.setCommande(commande);
    paiement.setWallet(wallet);

    return paiement;
  }
}
