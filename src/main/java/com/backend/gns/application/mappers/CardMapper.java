package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.CardRequest;
import com.backend.gns.application.dtos.responses.CardResponse;
import com.backend.gns.domain.models.Card;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CardMapper {

  private final StudentRepository studentRepository;

  public Card toEntity(CardRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête CardRequest ne peut pas être nulle");
    }

    Card card = new Card();
    card.setTrackingId(UUID.randomUUID());
    card.setQrCodeStaticUuid(request.qrCodeStaticUuid());
    card.setCardStatus(request.cardStatus());

    if (request.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(request.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Étudiant non trouvé avec l'ID: " + request.studentTrackingId()));
      card.setStudent(student);
    }

    return card;
  }

  public CardResponse toResponse(Card card) {
    if (card == null) {
      throw new IllegalArgumentException("L'entité Card ne peut pas être nulle");
    }

    return CardResponse.builder()
        .trackingId(card.getTrackingId())
        .qrCodeStaticUuid(card.getQrCodeStaticUuid())
        .cardStatus(card.getCardStatus())
        .studentTrackingId(card.getStudent() != null ? card.getStudent().getTrackingId() : null)
        .build();
  }

  public Card toEntityFromResponse(CardResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse CardResponse ne peut pas être nulle");
    }

    Card card = new Card();
    card.setTrackingId(response.trackingId());
    card.setQrCodeStaticUuid(response.qrCodeStaticUuid());
    card.setCardStatus(response.cardStatus());

    if (response.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(response.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Étudiant non trouvé avec l'ID: " + response.studentTrackingId()));
      card.setStudent(student);
    }

    return card;
  }
}
