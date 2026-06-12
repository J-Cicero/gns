package com.backend.gns.commerce.application.mappers;

import com.backend.gns.commerce.application.dtos.requests.CommandeRequest;
import com.backend.gns.commerce.application.dtos.responses.CommandeResponse;
import com.backend.gns.commerce.domain.models.Boutique;
import com.backend.gns.commerce.domain.models.Commande;
import com.backend.gns.commerce.infrastructure.repositories.BoutiqueRepository;
import com.backend.gns.student.domain.models.Student;
import com.backend.gns.student.infrastructure.repositories.StudentRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandeMapper {

  private final StudentRepository studentRepository;
  private final BoutiqueRepository boutiqueRepository;

  public Commande toEntity(CommandeRequest request) {
    if (request == null) return null;

    Commande commande = new Commande();
    commande.setTrackingId(UUID.randomUUID());
    commande.setReference("CMD-" + System.currentTimeMillis());
    commande.setDateCommande(LocalDateTime.now());
    commande.setMontantTotal(request.montantTotal());

    if (request.studentTrackingId() != null) {
      studentRepository.findByTrackingId(request.studentTrackingId())
          .ifPresent(commande::setStudent);
    }

    if (request.boutiqueTrackingId() != null) {
      boutiqueRepository.findByTrackingId(request.boutiqueTrackingId())
          .ifPresent(commande::setBoutique);
    }

    return commande;
  }

  public CommandeResponse toResponse(Commande commande) {
    if (commande == null) return null;

    return CommandeResponse.builder()
        .trackingId(commande.getTrackingId())
        .reference(commande.getReference())
        .dateCommande(commande.getDateCommande())
        .montantTotal(commande.getMontantTotal())
        .statut(commande.getStatut())
        .studentTrackingId(commande.getStudent() != null ? commande.getStudent().getTrackingId() : null)
        .boutiqueTrackingId(commande.getBoutique() != null ? commande.getBoutique().getTrackingId() : null)
        .build();
  }
}
