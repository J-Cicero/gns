package com.backend.gns.application.mappers;

import com.backend.gns.application.dtos.requests.CommandeRequest;
import com.backend.gns.application.dtos.responses.CommandeResponse;
import com.backend.gns.domain.enums.CommandeStatut;
import com.backend.gns.domain.models.Boutique;
import com.backend.gns.domain.models.Commande;
import com.backend.gns.domain.models.Student;
import com.backend.gns.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.infrastructure.repositories.StudentRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CommandeMapper {

  private final StudentRepository studentRepository;
  private final BoutiqueRepository boutiqueRepository;

  public CommandeMapper(
      StudentRepository studentRepository, BoutiqueRepository boutiqueRepository) {
    this.studentRepository = studentRepository;
    this.boutiqueRepository = boutiqueRepository;
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
    commande.setStatut(CommandeStatut.EN_ATTENTE);
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

    if (request.boutiqueTrackingId() != null) {
      Boutique boutique =
          boutiqueRepository
              .findByTrackingId(request.boutiqueTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Boutique non trouvée avec l'ID: " + request.boutiqueTrackingId()));
      commande.setBoutique(boutique);
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
        .boutiqueTrackingId(
            commande.getBoutique() != null ? commande.getBoutique().getTrackingId() : null)
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
    commande.setStatut(response.statut() != null ? response.statut() : CommandeStatut.EN_ATTENTE);

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

    if (response.boutiqueTrackingId() != null) {
      Boutique boutique =
          boutiqueRepository
              .findByTrackingId(response.boutiqueTrackingId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Boutique non trouvée avec l'ID: " + response.boutiqueTrackingId()));
      commande.setBoutique(boutique);
    }

    return commande;
  }
}
