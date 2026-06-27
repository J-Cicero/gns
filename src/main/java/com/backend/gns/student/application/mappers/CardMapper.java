package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.CardRequest;
import com.backend.gns.student.application.dtos.responses.CardResponse;
import com.backend.gns.student.domain.models.Card;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CardMapper {

  private final WalletRepository walletRepository;

  public Card toEntity(CardRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("CardRequest cannot be null");
    }

    Card card = new Card();
    card.setTrackingId(UUID.randomUUID());
    card.setCardNumber(request.cardNumber());
    card.setQrCodeData(request.qrCodeData());
    card.setStatus(request.status());

    if (request.walletTrackingId() != null) {
      Wallet wallet =
          walletRepository
              .findByTrackingId(request.walletTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Wallet not found with trackingId: " + request.walletTrackingId()));
      card.setWallet(wallet);
    }

    return card;
  }

  public CardResponse toResponse(Card card) {
    if (card == null) {
      return null;
    }

    return CardResponse.builder()
        .trackingId(card.getTrackingId())
        .cardNumber(card.getCardNumber())
        .qrCodeData(card.getQrCodeData())
        .status(card.getStatus())
        .emissionDate(card.getEmissionDate())
        .expirationDate(card.getExpirationDate())
        .walletTrackingId(card.getWallet() != null ? card.getWallet().getTrackingId() : null)
        .studentNom((card.getWallet() != null && card.getWallet().getStudent() != null) ? card.getWallet().getStudent().getLastName() : null)
        .studentPrenom((card.getWallet() != null && card.getWallet().getStudent() != null) ? card.getWallet().getStudent().getFirstName() : null)
        .build();
  }
}
