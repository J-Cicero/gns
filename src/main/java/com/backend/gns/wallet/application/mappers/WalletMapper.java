package com.backend.gns.wallet.application.mappers;

import com.backend.gns.wallet.application.dtos.requests.WalletRequest;
import com.backend.gns.wallet.application.dtos.responses.WalletResponse;
import com.backend.gns.wallet.domain.models.Wallet;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

  public Wallet toEntity(WalletRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête WalletRequest ne peut pas être nulle");
    }

    Wallet wallet = new Wallet();
    wallet.setTrackingId(UUID.randomUUID());
    wallet.setTypeWallet(request.typeWallet());
    wallet.setStatutWallet(request.statutWallet());
    wallet.setSolde(request.solde());
    wallet.setPlafond(request.plafond());
    wallet.setDateCreation(
        request.dateCreation() != null ? request.dateCreation() : LocalDateTime.now());

    return wallet;
  }

  public WalletResponse toResponse(Wallet wallet) {
    if (wallet == null) {
      throw new IllegalArgumentException("L'entité Wallet ne peut pas être nulle");
    }

    return WalletResponse.builder()
        .trackingId(wallet.getTrackingId())
        .typeWallet(wallet.getTypeWallet())
        .statutWallet(wallet.getStatutWallet())
        .solde(wallet.getSolde())
        .plafond(wallet.getPlafond())
        .dateCreation(wallet.getDateCreation())
        .build();
  }

  public Wallet toEntityFromResponse(WalletResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse WalletResponse ne peut pas être nulle");
    }

    Wallet wallet = new Wallet();
    wallet.setTrackingId(response.trackingId());
    wallet.setTypeWallet(response.typeWallet());
    wallet.setStatutWallet(response.statutWallet());
    wallet.setSolde(response.solde());
    wallet.setPlafond(response.plafond());
    wallet.setDateCreation(response.dateCreation());

    return wallet;
  }
}
