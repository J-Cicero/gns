package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.CommandeRequest;
import com.backend.gns.application.dtos.responses.CommandeResponse;
import com.backend.gns.domain.enums.CommandeStatut;
import com.backend.gns.domain.models.Commande;
import com.backend.gns.domain.models.Merchant;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.MerchantRepository;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CommandeMapper {

  private final StudentRepository studentRepository;
  private final MerchantRepository merchantRepository;

  public CommandeMapper(
      StudentRepository studentRepository, MerchantRepository merchantRepository) {
    this.studentRepository = studentRepository;
    this.merchantRepository = merchantRepository;
  }

  public Commande toEntity(CommandeRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("La requête CommandeRequest ne peut pas être nulle");
    }

    Commande commande = new Commande();
    commande.setTrackingId(UUID.randomUUID());
    commande.setReference(request.reference());
    commande.setDateCommande(
        request.dateCommande() != null ? request.dateCommande() : LocalDateTime.now());
    commande.setStatut(CommandeStatut.EN_COURS);
    commande.setMontantTotal(request.montantTotal());

    if (request.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(request.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Étudiant non trouvé avec l'ID: " + request.studentTrackingId()));
      commande.setStudent(student);
    }

    if (request.merchantTrackingId() != null) {
      Merchant merchant =
          merchantRepository
              .findByTrackingId(request.merchantTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Commerçant non trouvé avec l'ID: " + request.merchantTrackingId()));
      commande.setMerchant(merchant);
    }

    return commande;
  }

  public CommandeResponse toResponse(Commande commande) {
    if (commande == null) {
      throw new IllegalArgumentException("L'entité Commande ne peut pas être nulle");
    }

    return CommandeResponse.builder()
        .trackingId(commande.getTrackingId())
        .reference(commande.getReference())
        .dateCommande(commande.getDateCommande())
        .montantTotal(commande.getMontantTotal())
        .statut(commande.getStatut())
        .studentTrackingId(
            commande.getStudent() != null ? commande.getStudent().getTrackingId() : null)
        .merchantTrackingId(
            commande.getMerchant() != null ? commande.getMerchant().getTrackingId() : null)
        .build();
  }

  public Commande toEntityFromResponse(CommandeResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse CommandeResponse ne peut pas être nulle");
    }

    Commande commande = new Commande();
    commande.setTrackingId(response.trackingId());
    commande.setReference(response.reference());
    commande.setDateCommande(response.dateCommande());
    commande.setMontantTotal(response.montantTotal());
    commande.setStatut(response.statut() != null ? response.statut() : CommandeStatut.EN_COURS);

    if (response.studentTrackingId() != null) {
      Student student =
          studentRepository
              .findByTrackingId(response.studentTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Étudiant non trouvé avec l'ID: " + response.studentTrackingId()));
      commande.setStudent(student);
    }

    if (response.merchantTrackingId() != null) {
      Merchant merchant =
          merchantRepository
              .findByTrackingId(response.merchantTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Commerçant non trouvé avec l'ID: " + response.merchantTrackingId()));
      commande.setMerchant(merchant);
    }

    return commande;
  }
}
