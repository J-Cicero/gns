package com.backend.gns.wallet.application.mappers;

import com.backend.gns.wallet.application.dtos.requests.VersementRequest;
import com.backend.gns.wallet.application.dtos.responses.VersementResponse;
import com.backend.gns.wallet.domain.models.Versement;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
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
      throw new IllegalArgumentException("VersementRequest cannot be null");
    }

    Versement versement = new Versement();
    versement.setTrackingId(UUID.randomUUID());
    versement.setMontantVerse(request.amount());
    versement.setTypeVersement(request.paymentType());
    versement.setDateVersement(LocalDateTime.now());
    versement.setStatut(request.status());

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Wallet not found with trackingId: " + request.walletTrackingId()));
      versement.setWallet(wallet);
    }

    return versement;
  }

  public VersementResponse toResponse(Versement versement) {
    if (versement == null) {
      return null;
    }

    return VersementResponse.builder()
        .trackingId(versement.getTrackingId())
        .amount(versement.getMontantVerse())
        .paymentType(versement.getTypeVersement())
        .paymentDate(versement.getDateVersement())
        .status(versement.getStatut())
        .walletTrackingId(
            versement.getWallet() != null ? versement.getWallet().getTrackingId() : null)
        .build();
  }
}
