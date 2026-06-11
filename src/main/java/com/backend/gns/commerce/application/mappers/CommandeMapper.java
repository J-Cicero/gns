package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.domain.enums.CommandeStatut;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.application.dtos.requests.CommandeRequest;
import com.backend.gns.commerce.application.dtos.responses.CommandeResponse;
import com.backend.gns.commerce.domain.models.Commande;
import com.backend.gns.student.domain.models.Student;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CommandeMapper {

  public Commande toEntity(CommandeRequest request, Student student, Boutique boutique) {
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

    commande.setStudent(student);
    commande.setBoutique(boutique);

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

  public Commande toEntityFromResponse(
      CommandeResponse response, Student student, Boutique boutique) {
    if (response == null) {
      throw new IllegalArgumentException("La réponse CommandeResponse ne peut pas être nulle");
    }

    Commande commande = new Commande();
    commande.setTrackingId(response.trackingId());
    commande.setReference(response.reference());
    commande.setDateCommande(response.dateCommande());
    commande.setMontantTotal(response.montantTotal());
    commande.setStatut(response.statut() != null ? response.statut() : CommandeStatut.EN_ATTENTE);

    commande.setStudent(student);
    commande.setBoutique(boutique);

    return commande;
  }
}
