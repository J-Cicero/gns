package com.backend.gns.student.application.mappers;

import com.backend.gns.student.application.dtos.requests.CardRequest;
import com.backend.gns.student.application.dtos.responses.CardResponse;
import com.backend.gns.student.domain.models.Card;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import com.backend.gns.wallet.domain.models.Wallet;
import com.backend.gns.wallet.infrastructure.repositories.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CardMapper {

  private final WalletRepository walletRepository;
  private final StudentRepository studentRepository;

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

    String studentNom = null;
    String studentPrenom = null;

    if (card.getWallet() != null && card.getWallet().getTrackingId() != null) {
      Student student = card.getWallet().getStudent();
      if (student == null) {
        student = studentRepository.findByWalletTrackingId(card.getWallet().getTrackingId()).orElse(null);
      }
      if (student != null) {
        studentNom = student.getLastName();
        studentPrenom = student.getFirstName();
      }
    }

    return CardResponse.builder()
        .trackingId(card.getTrackingId())
        .cardNumber(card.getCardNumber())
        .qrCodeData(card.getQrCodeData())
        .status(card.getStatus())
        .emissionDate(card.getEmissionDate())
        .expirationDate(card.getExpirationDate())
        .walletTrackingId(card.getWallet() != null ? card.getWallet().getTrackingId() : null)
        .studentNom(studentNom)
        .studentPrenom(studentPrenom)
        .build();
  }
}
