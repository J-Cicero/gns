package com.backend.gns.Shared.wallet.application.mappers;

import com.backend.gns.Shared.wallet.application.dtos.requests.VersementRequest;
import com.backend.gns.Shared.wallet.application.dtos.responses.VersementResponse;
import com.backend.gns.Shared.wallet.domain.models.Versement;
import com.backend.gns.Shared.wallet.domain.models.Wallet;

import com.backend.gns.Shared.wallet.infrastructure.repositories.WalletRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class VersementMapper {

  private final WalletRepository walletRepository;

  public VersementMapper(WalletRepository walletRepository) {
    this.walletRepository = walletRepository;
  }

  public Versement toEntity(VersementRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête VersementRequest ne peut pas être nulle");
    }

    Versement versement = new Versement();
    versement.setTrackingId(UUID.randomUUID());
    versement.setMontantVerse(request.montantVerse());
    versement.setTypeVersement(request.typeVersement());
    versement.setDateVersement(LocalDateTime.now());
    versement.setStatut(request.statut());

    if (request.trackingWalletId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.trackingWalletId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + request.trackingWalletId()));
      versement.setWallet(wallet);
    }

    return versement;
  }

  public VersementResponse toResponse(Versement versement) {
    if (versement == null) {
      throw new IllegalArgumentException("L'entité Versement ne peut pas être nulle");
    }

    return VersementResponse.builder()
        .trackingId(versement.getTrackingId())
        .montantVerse(versement.getMontantVerse())
        .typeVersement(versement.getTypeVersement())
        .dateVersement(LocalDateTime.now())
        .statut(versement.getStatut())
        .trackingWalletId(
            versement.getWallet() != null ? versement.getWallet().getTrackingId() : null)
        .build();
  }

  public Versement toEntityFromResponse(VersementResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse VersementResponse ne peut pas être nulle");
    }

    Versement versement = new Versement();
    versement.setTrackingId(response.trackingId());
    versement.setMontantVerse(response.montantVerse());
    versement.setTypeVersement(response.typeVersement());
    versement.setDateVersement(LocalDateTime.now());
    versement.setStatut(response.statut());

    if (response.trackingWalletId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(response.trackingWalletId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Portefeuille non trouvé avec l'ID: " + response.trackingWalletId()));
      versement.setWallet(wallet);
    }
    return versement;
  }
}
